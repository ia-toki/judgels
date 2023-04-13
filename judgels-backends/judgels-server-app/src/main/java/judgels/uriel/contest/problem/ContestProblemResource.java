package judgels.uriel.contest.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;
import static judgels.service.actor.Actors.GUEST;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.programming.ProblemWorksheet;
import judgels.sandalphon.problem.ProblemClient;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemsResponse;
import judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;

public class ContestProblemResource implements ContestProblemService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestModuleStore moduleStore;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final SubmissionStore submissionStore;
    private final ProblemClient problemClient;

    @Inject
    public ContestProblemResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestModuleStore moduleStore,
            ContestProblemRoleChecker problemRoleChecker,
            ContestProblemStore problemStore,
            SubmissionStore submissionStore,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.moduleStore = moduleStore;
        this.problemRoleChecker = problemRoleChecker;
        this.problemStore = problemStore;
        this.submissionStore = submissionStore;
        this.problemClient = problemClient;
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

        Map<String, String> slugToJidMap = problemClient.translateAllowedSlugsToJids(actorJid, slugs);

        Set<String> notAllowedSlugs = data.stream()
                .map(ContestProblemData::getSlug)
                .filter(slug -> !slugToJidMap.containsKey(slug))
                .collect(Collectors.toSet());

        if (!notAllowedSlugs.isEmpty()) {
            throw ContestErrors.problemSlugsNotAllowed(notAllowedSlugs);
        }

        List<ContestProblem> setData = data.stream().map(problem ->
                new ContestProblem.Builder()
                        .alias(problem.getAlias())
                        .problemJid(slugToJidMap.get(problem.getSlug()))
                        .status(problem.getStatus())
                        .submissionsLimit(problem.getSubmissionsLimit())
                        .points(problem.getPoints().orElse(0))
                        .build())
                .collect(Collectors.toList());

        problemStore.setProblems(contestJid, setData);

        contestLogger.log(contestJid, "SET_PROBLEMS");
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestProblemsResponse getProblems(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canView(actorJid, contest));

        List<ContestProblem> problems = problemStore.getProblems(contestJid);
        Set<String> problemJids = problems.stream().map(ContestProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, ProblemInfo> problemsMap = problemClient.getProblems(problemJids);
        Map<String, Long> totalSubmissionsMap =
                submissionStore.getTotalSubmissionsMap(contestJid, actorJid, problemJids);

        boolean canManage = problemRoleChecker.canManage(actorJid, contest);
        ContestProblemConfig config = new ContestProblemConfig.Builder()
                .canManage(canManage)
                .build();

        if (!canManage && !problemRoleChecker.canView(GUEST, contest)) {
            // hide slugs in non-public contests from non-managers.
            problemsMap = problemsMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> new ProblemInfo.Builder()
                                    .from(e.getValue())
                                    .slug(Optional.empty())
                                    .build()));
        }

        contestLogger.log(contestJid, "OPEN_PROBLEMS");

        return new ContestProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .totalSubmissionsMap(totalSubmissionsMap)
                .config(config)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet getProgrammingProblemWorksheet(
            Optional<AuthHeader> authHeader,
            String contestJid,
            String problemAlias,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canView(actorJid, contest));

        ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = problemClient.getProblem(problemJid);

        if (problemInfo.getType() != ProblemType.PROGRAMMING) {
            throw ContestErrors.wrongProblemType(problemInfo.getType());
        }

        long totalSubmissions = submissionStore.getTotalSubmissions(contestJid, actorJid, problemJid);

        Optional<String> reasonNotAllowedToSubmit =
                problemRoleChecker.canSubmit(actorJid, contest, problem, totalSubmissions);

        ProblemWorksheet worksheet = problemClient.getProgrammingProblemWorksheet(problemJid, language);

        LanguageRestriction contestGradingLanguageRestriction =
                moduleStore.getStyleModuleConfig(contestJid, contest.getStyle()).getGradingLanguageRestriction();
        LanguageRestriction problemGradingLanguageRestriction =
                worksheet.getSubmissionConfig().getGradingLanguageRestriction();
        LanguageRestriction combinedGradingLanguageRestriction =
                LanguageRestriction.combine(contestGradingLanguageRestriction, problemGradingLanguageRestriction);

        ProblemWorksheet finalWorksheet = new ProblemWorksheet.Builder()
                .from(worksheet)
                .submissionConfig(new ProblemSubmissionConfig.Builder()
                        .from(worksheet.getSubmissionConfig())
                        .gradingLanguageRestriction(combinedGradingLanguageRestriction)
                        .build())
                .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                .build();

        contestLogger.log(contestJid, "OPEN_PROBLEM", null, problemJid);

        return new judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet.Builder()
                .defaultLanguage(problemInfo.getDefaultLanguage())
                .languages(problemInfo.getTitlesByLanguage().keySet())
                .problem(problem)
                .totalSubmissions(totalSubmissions)
                .worksheet(finalWorksheet)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestProblemWorksheet getBundleProblemWorksheet(
            Optional<AuthHeader> authHeader,
            String contestJid,
            String problemAlias,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(problemRoleChecker.canView(actorJid, contest));

        ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = problemClient.getProblem(problemJid);

        if (problemInfo.getType() != ProblemType.BUNDLE) {
            throw ContestErrors.wrongProblemType(problemInfo.getType());
        }

        long totalSubmissions = submissionStore.getTotalSubmissions(contestJid, actorJid, problemJid);

        Optional<String> reasonNotAllowedToSubmit =
                problemRoleChecker.canSubmit(actorJid, contest, problem, totalSubmissions);

        judgels.sandalphon.api.problem.bundle.ProblemWorksheet worksheet =
                problemClient.getBundleProblemWorksheetWithoutAnswerKey(problemJid, language);

        judgels.sandalphon.api.problem.bundle.ProblemWorksheet
                finalWorksheet = new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                .from(worksheet)
                .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                .build();

        contestLogger.log(contestJid, "OPEN_PROBLEM", null, problemJid);

        return new ContestProblemWorksheet.Builder()
                .defaultLanguage(problemInfo.getDefaultLanguage())
                .languages(problemInfo.getTitlesByLanguage().keySet())
                .problem(problem)
                .totalSubmissions(totalSubmissions)
                .worksheet(finalWorksheet)
                .build();
    }
}
