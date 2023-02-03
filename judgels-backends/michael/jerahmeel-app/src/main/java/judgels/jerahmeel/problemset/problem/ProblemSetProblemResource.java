package judgels.jerahmeel.problemset.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problem.ProblemDifficulty;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemTopStats;
import judgels.jerahmeel.api.problemset.ProblemSetErrors;
import judgels.jerahmeel.api.problemset.problem.ProblemEditorialResponse;
import judgels.jerahmeel.api.problemset.problem.ProblemReportResponse;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemData;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemService;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemWorksheet;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemsResponse;
import judgels.jerahmeel.difficulty.ProblemDifficultyStore;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jerahmeel.uriel.ContestClient;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemMetadata;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.ContestInfo;

public class ProblemSetProblemResource implements ProblemSetProblemService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ProblemSetStore problemSetStore;
    private final ProblemSetProblemStore problemStore;
    private final ProblemDifficultyStore difficultyStore;
    private final UserClient userClient;
    private final ProblemClient problemClient;
    private final ContestClient contestClient;
    private final StatsStore statsStore;

    @Inject
    public ProblemSetProblemResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ProblemSetStore problemSetStore,
            ProblemSetProblemStore problemStore,
            ProblemDifficultyStore difficultyStore,
            UserClient userClient,
            ProblemClient problemClient,
            ContestClient contestClient,
            StatsStore statsStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.problemSetStore = problemSetStore;
        this.problemStore = problemStore;
        this.difficultyStore = difficultyStore;
        this.userClient = userClient;
        this.problemClient = problemClient;
        this.contestClient = contestClient;
        this.statsStore = statsStore;
    }

    @Override
    @UnitOfWork
    public void setProblems(AuthHeader authHeader, String problemSetJid, List<ProblemSetProblemData> data) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> aliases = data.stream().map(ProblemSetProblemData::getAlias).collect(Collectors.toSet());
        Set<String> slugs = data.stream().map(ProblemSetProblemData::getSlug).collect(Collectors.toSet());

        checkArgument(data.size() <= 100, "Cannot set more than 100 problems.");
        checkArgument(aliases.size() == data.size(), "Problem aliases must be unique");
        checkArgument(slugs.size() == data.size(), "Problem slugs must be unique");

        Map<String, String> slugToJidMap = problemClient.translateAllowedSlugsToJids(actorJid, slugs);

        Set<String> contestSlugs = data.stream()
                .map(ProblemSetProblemData::getContestSlugs)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Map<String, String> contestSlugToJidMap = contestClient.translateSlugsToJids(contestSlugs);

        Set<String> notAllowedContestSlugs = data.stream()
                .map(ProblemSetProblemData::getContestSlugs)
                .flatMap(List::stream)
                .filter(slug -> !contestSlugToJidMap.containsKey(slug))
                .collect(Collectors.toSet());

        if (!notAllowedContestSlugs.isEmpty()) {
            throw ProblemSetErrors.contestSlugsNotAllowed(notAllowedContestSlugs);
        }

        List<ProblemSetProblem> setData = data.stream().filter(cp -> slugToJidMap.containsKey(cp.getSlug())).map(p ->
                new ProblemSetProblem.Builder()
                        .alias(p.getAlias())
                        .problemJid(slugToJidMap.get(p.getSlug()))
                        .type(p.getType())
                        .contestJids(p.getContestSlugs().stream()
                                .filter(contestSlugToJidMap::containsKey)
                                .map(contestSlugToJidMap::get)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        Map<String, Boolean> problemVisibilitiesMap = problemStore.setProblems(problemSetJid, setData);
        problemClient.setProblemVisibilityTagsByJids(problemVisibilitiesMap);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetProblemsResponse getProblems(Optional<AuthHeader> authHeader, String problemSetJid) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        List<ProblemSetProblem> problems = problemStore.getProblems(problemSetJid);
        Set<String> problemJids = problems.stream()
                .map(ProblemSetProblem::getProblemJid)
                .collect(Collectors.toSet());
        Set<String> contestJids = problems.stream()
                .map(ProblemSetProblem::getContestJids)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        return new ProblemSetProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemClient.getProblems(problemJids))
                .problemMetadatasMap(problemClient.getProblemMetadatas(problemJids))
                .problemDifficultiesMap(difficultyStore.getProblemDifficultiesMap(problemJids))
                .problemProgressesMap(statsStore.getProblemProgressesMap(actorJid, problemJids))
                .contestsMap(roleChecker.isAdmin(actorJid)
                        ? contestClient.getContestsByJids(contestJids)
                        : ImmutableMap.of())
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetProblem getProblem(
            Optional<AuthHeader> authHeader,
            String problemSetJid,
            String problemAlias) {

        actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        return checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetProblemWorksheet getProblemWorksheet(
            Optional<AuthHeader> authHeader,
            String problemSetJid,
            String problemAlias,
            Optional<String> language) {

        actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        ProblemSetProblem problem = checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = problemClient.getProblem(problemJid);

        Optional<String> reasonNotAllowedToSubmit = authHeader.isPresent()
                ? Optional.empty()
                : Optional.of("You must log in to submit.");

        if (problemInfo.getType() == ProblemType.PROGRAMMING) {
            return new judgels.jerahmeel.api.problemset.problem.programming.ProblemSetProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .worksheet(new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder()
                            .from(problemClient.getProgrammingProblemWorksheet(problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        } else {
            return new judgels.jerahmeel.api.problemset.problem.bundle.ProblemSetProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .worksheet(new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                            .from(problemClient.getBundleProblemWorksheetWithoutAnswerKey(problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        }
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemReportResponse getProblemReport(
            Optional<AuthHeader> authHeader,
            String problemSetJid,
            String problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        ProblemSetProblem problem = checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
        String problemJid = problem.getProblemJid();
        Set<String> problemJids = ImmutableSet.of(problemJid);

        ProblemMetadata metadata = problemClient.getProblemMetadata(problem.getProblemJid());
        ProblemDifficulty difficulty = difficultyStore.getProblemDifficultiesMap(problemJids).get(problemJid);
        ProblemTopStats topStats = statsStore.getProblemTopStats(problemJid);
        ProblemProgress progress = statsStore.getProblemProgressesMap(actorJid, problemJids).get(problemJid);

        Map<String, ContestInfo> contestsMap = contestClient.getContestsByJids(problem.getContestJids());
        List<ContestInfo> contests = problem.getContestJids().stream()
                .filter(contestsMap::containsKey)
                .map(contestsMap::get)
                .collect(Collectors.toList());

        Set<String> userJids = new HashSet<>();
        metadata.getSettersMap().values().forEach(userJids::addAll);
        topStats.getTopUsersByScore().forEach(e -> userJids.add(e.getUserJid()));
        topStats.getTopUsersByTime().forEach(e -> userJids.add(e.getUserJid()));
        topStats.getTopUsersByMemory().forEach(e -> userJids.add(e.getUserJid()));
        Map<String, Profile> profilesMap = userClient.getProfiles(userJids);

        return new ProblemReportResponse.Builder()
                .metadata(metadata)
                .difficulty(difficulty)
                .topStats(topStats)
                .progress(progress)
                .contests(contests)
                .profilesMap(profilesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemEditorialResponse getProblemEditorial(
            String problemSetJid,
            String problemAlias,
            Optional<String> language) {

        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        ProblemSetProblem problem = checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
        ProblemEditorialInfo editorial = problemClient.getProblemEditorial(problem.getProblemJid(), language);
        return new ProblemEditorialResponse.Builder()
                .editorial(editorial)
                .build();
    }
}
