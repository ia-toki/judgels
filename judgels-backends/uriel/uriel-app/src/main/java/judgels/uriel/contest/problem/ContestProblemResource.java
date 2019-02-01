package judgels.uriel.contest.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static judgels.sandalphon.SandalphonUtils.combineLanguageRestrictions;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.ProblemWorksheet;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemWorksheet;
import judgels.uriel.api.contest.problem.ContestProblemsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.submission.ContestSubmissionStore;

public class ContestProblemResource implements ContestProblemService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestModuleStore moduleStore;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final ContestSubmissionStore submissionStore;
    private final SandalphonClientConfiguration sandalphonConfig;
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
            SandalphonClientConfiguration sandalphonConfig,
            @Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader,
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
    public void setProblems(
            AuthHeader authHeader,
            String contestJid,
            List<ContestProblemData> data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canManage(actorJid, contest));

        Set<String> aliases = data.stream().map(ContestProblemData::getAlias).collect(Collectors.toSet());
        Set<String> slugs = data.stream().map(ContestProblemData::getSlug).collect(Collectors.toSet());

        checkArgument(data.size() <= 100, "Cannot set more than 100 problems.");
        checkArgument(aliases.size() == data.size(), "Problem aliases must be unique");
        checkArgument(slugs.size() == data.size(), "Problem slugs must be unique");

        Map<String, String> slugToJidMap = clientProblemService.translateAllowedSlugsToJids(
                sandalphonClientAuthHeader,
                actorJid,
                slugs);

        Set<String> notAllowedSlugs = data.stream()
                .map(ContestProblemData::getSlug)
                .filter(slug -> !slugToJidMap.containsKey(slug))
                .collect(Collectors.toSet());

        if (!notAllowedSlugs.isEmpty()) {
            throw ContestErrors.problemSlugsNotAllowed(notAllowedSlugs);
        }

        List<ContestProblem> setData = Lists.transform(data, problem ->
                new ContestProblem.Builder()
                        .alias(problem.getAlias())
                        .problemJid(slugToJidMap.get(problem.getSlug()))
                        .status(problem.getStatus())
                        .submissionsLimit(problem.getSubmissionsLimit())
                        .build());

        problemStore.setProblems(contestJid, setData);
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
                : clientProblemService.getProblems(sandalphonClientAuthHeader, problemJids);
        Map<String, Long> totalSubmissionsMap =
                submissionStore.getTotalSubmissionsMap(contestJid, actorJid, problemJids);

        boolean canManage = problemRoleChecker.canManage(actorJid, contest);
        ContestProblemConfig config = new ContestProblemConfig.Builder()
                .canManage(canManage)
                .build();

        return new ContestProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .totalSubmissionsMap(totalSubmissionsMap)
                .config(config)
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
                moduleStore.getStyleModuleConfig(contestJid, contest.getStyle()).getGradingLanguageRestriction();
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
                .languages(problemInfo.getTitlesByLanguage().keySet())
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
