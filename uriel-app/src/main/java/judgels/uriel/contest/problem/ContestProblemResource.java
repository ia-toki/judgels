package judgels.uriel.contest.problem;

import static judgels.sandalphon.SandalphonUtils.combineLanguageRestrictions;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

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
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestContestantProblemWorksheet;
import judgels.uriel.api.contest.problem.ContestContestantProblemsResponse;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.style.ContestStyleStore;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;
import judgels.uriel.sandalphon.SandalphonConfiguration;

public class ContestProblemResource implements ContestProblemService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestStyleStore styleStore;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final SandalphonConfiguration sandalphonConfig;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestProblemResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestStyleStore styleStore,
            ContestProblemRoleChecker problemRoleChecker,
            ContestProblemStore problemStore,
            SandalphonConfiguration sandalphonConfig,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.styleStore = styleStore;
        this.problemRoleChecker = problemRoleChecker;
        this.problemStore = problemStore;
        this.sandalphonConfig = sandalphonConfig;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantProblemsResponse getMyProblems(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canViewProblems(actorJid, contest));

        List<ContestContestantProblem> contestantProblems = problemStore.getContestantProblems(contestJid, actorJid);
        Set<String> problemJids =
                contestantProblems.stream().map(p -> p.getProblem().getProblemJid()).collect(Collectors.toSet());

        Map<String, ProblemInfo> problemsMap = clientProblemService.findProblemsByJids(
                sandalphonClientAuthHeader,
                problemJids);

        return new ContestContestantProblemsResponse.Builder()
                .data(contestantProblems)
                .problemsMap(problemsMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantProblemWorksheet getProblemWorksheet(
            Optional<AuthHeader> authHeader,
            String contestJid,
            String problemAlias,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canViewProblems(actorJid, contest));

        ContestContestantProblem contestantProblem =
                checkFound(problemStore.findContestantProblemByAlias(contestJid, actorJid, problemAlias));
        String problemJid = contestantProblem.getProblem().getProblemJid();

        ProblemInfo problem = clientProblemService.getProblem(sandalphonClientAuthHeader, problemJid);

        Optional<String> reasonNotAllowedToSubmit =
                problemRoleChecker.canSubmitProblem(actorJid, contest, contestantProblem);

        ProblemWorksheet worksheet =
                clientProblemService.getProblemWorksheet(sandalphonClientAuthHeader, problemJid, language);

        LanguageRestriction contestGradingLanguageRestriction =
                styleStore.getStyleConfig(contestJid).getGradingLanguageRestriction();
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

        return new ContestContestantProblemWorksheet.Builder()
                .defaultLanguage(problem.getDefaultLanguage())
                .languages(problem.getNamesByLanguage().keySet())
                .contestantProblem(contestantProblem)
                .worksheet(finalWorksheet)
                .build();
    }

    private String replaceRenderUrls(String statementText, String problemJid) {
        return statementText.replaceAll(
                "src=\"render/",
                String.format("src=\"%s/api/v2/problems/%s/render/", sandalphonConfig.getBaseUrl(), problemJid));
    }
}
