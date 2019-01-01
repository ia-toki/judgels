package judgels.uriel.contest.problem;

import static judgels.sandalphon.SandalphonUtils.combineLanguageRestrictions;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.ProblemWorksheet;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemWorksheet;
import judgels.uriel.api.contest.problem.ContestProblemsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.submission.ContestSubmissionStore;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;
import judgels.uriel.sandalphon.SandalphonConfiguration;

public class ContestProblemResource implements ContestProblemService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestModuleStore moduleStore;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final ContestSubmissionStore submissionStore;
    private final SandalphonConfiguration sandalphonConfig;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestProblemResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestModuleStore moduleStore,
            ContestProblemRoleChecker problemRoleChecker,
            ContestProblemStore problemStore,
            ContestSubmissionStore submissionStore,
            SandalphonConfiguration sandalphonConfig,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.moduleStore = moduleStore;
        this.problemRoleChecker = problemRoleChecker;
        this.problemStore = problemStore;
        this.submissionStore = submissionStore;
        this.sandalphonConfig = sandalphonConfig;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork
    public void upsertProblem(AuthHeader authHeader, String contestJid, ContestProblemData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canSupervise(actorJid, contest));

        problemStore.upsertProblem(contestJid, data);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestProblemsResponse getProblems(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canView(actorJid, contest));

        List<ContestProblem> problems = problemStore.getProblems(contestJid);
        Set<String> problemJids = problems.stream().map(ContestProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, ProblemInfo> problemsMap = problemJids.isEmpty()
                ? ImmutableMap.of()
                : clientProblemService.getProblemsByJids(sandalphonClientAuthHeader, problemJids);
        Map<String, Long> totalSubmissionsMap =
                submissionStore.getTotalSubmissionsMap(contestJid, actorJid, problemJids);

        return new ContestProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .totalSubmissionsMap(totalSubmissionsMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestProblemWorksheet getProblemWorksheet(
            Optional<AuthHeader> authHeader,
            String contestJid,
            String problemAlias,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canView(actorJid, contest));

        ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = clientProblemService.getProblem(sandalphonClientAuthHeader, problemJid);

        long totalSubmissions = submissionStore.getTotalSubmissions(contestJid, actorJid, problemJid);

        Optional<String> reasonNotAllowedToSubmit =
                problemRoleChecker.canSubmit(actorJid, contest, problem, totalSubmissions);

        ProblemWorksheet worksheet =
                clientProblemService.getProblemWorksheet(sandalphonClientAuthHeader, problemJid, language);

        LanguageRestriction contestGradingLanguageRestriction =
                moduleStore.getStyleModuleConfig(contestJid).getGradingLanguageRestriction();
        LanguageRestriction problemGradingLanguageRestriction =
                worksheet.getSubmissionConfig().getGradingLanguageRestriction();
        LanguageRestriction combinedGradingLanguageRestriction =
                combineLanguageRestrictions(contestGradingLanguageRestriction, problemGradingLanguageRestriction);

        ProblemWorksheet finalWorksheet = new ProblemWorksheet.Builder()
                .from(worksheet)
                .statement(new ProblemStatement.Builder()
                        .from(worksheet.getStatement())
                        .text(replaceRenderUrls(worksheet.getStatement().getText(), problemJid))
                        .build())
                .submissionConfig(new ProblemSubmissionConfig.Builder()
                        .from(worksheet.getSubmissionConfig())
                        .gradingLanguageRestriction(combinedGradingLanguageRestriction)
                        .build())
                .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                .build();

        return new ContestProblemWorksheet.Builder()
                .defaultLanguage(problemInfo.getDefaultLanguage())
                .languages(problemInfo.getNamesByLanguage().keySet())
                .problem(problem)
                .totalSubmissions(totalSubmissions)
                .worksheet(finalWorksheet)
                .build();
    }

    private String replaceRenderUrls(String statementText, String problemJid) {
        String baseUrl = sandalphonConfig.getBaseUrl();
        return statementText
                .replaceAll(
                        "src=\"render/",
                        String.format("src=\"%s/api/v2/problems/%s/render/", baseUrl, problemJid))
                .replaceAll(
                        "href=\"render/",
                        String.format("href=\"%s/api/v2/problems/%s/render/", baseUrl, problemJid));
    }
}
