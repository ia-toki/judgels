package judgels.jerahmeel.problemset.problem;

import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemStats;
import judgels.jerahmeel.api.problem.ProblemTopStats;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemService;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemWorksheet;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemsResponse;
import judgels.jerahmeel.api.problemset.problem.ProblemStatsResponse;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemSetProblemResource implements ProblemSetProblemService {
    private final ActorChecker actorChecker;
    private final ProblemSetStore problemSetStore;
    private final ProblemSetProblemStore problemStore;
    private final ProblemClient problemClient;
    private final StatsStore statsStore;
    private final ProfileService profileService;

    @Inject
    public ProblemSetProblemResource(
            ActorChecker actorChecker,
            ProblemSetStore problemSetStore,
            ProblemSetProblemStore problemStore,
            ProblemClient problemClient,
            StatsStore statsStore,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.problemSetStore = problemSetStore;
        this.problemStore = problemStore;
        this.problemClient = problemClient;
        this.statsStore = statsStore;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetProblemsResponse getProblems(Optional<AuthHeader> authHeader, String problemSetJid) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        List<ProblemSetProblem> problems = problemStore.getProblems(problemSetJid);
        Set<String> problemJids = problems.stream().map(ProblemSetProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, ProblemInfo> problemsMap = problemClient.getProblems(problemJids);
        Map<String, ProblemProgress> problemProgressesMap = statsStore.getProblemProgressesMap(actorJid, problemJids);
        Map<String, ProblemStats> problemStatsMap = statsStore.getProblemStatsMap(problemJids);

        return new ProblemSetProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .problemProgressesMap(problemProgressesMap)
                .problemStatsMap(problemStatsMap)
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
    public ProblemStatsResponse getProblemStats(
            Optional<AuthHeader> authHeader,
            String problemSetJid,
            String problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        ProblemSetProblem problem = checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
        String problemJid = problem.getProblemJid();

        ProblemProgress progress = statsStore
                .getProblemProgressesMap(actorJid, ImmutableSet.of(problemJid)).get(problemJid);
        ProblemStats stats = statsStore.getProblemStatsMap(ImmutableSet.of(problemJid)).get(problemJid);
        ProblemTopStats topStats = statsStore.getProblemTopStats(problemJid);

        Set<String> userJids = new HashSet<>();
        topStats.getTopUsersByScore().forEach(e -> userJids.add(e.getUserJid()));
        topStats.getTopUsersByTime().forEach(e -> userJids.add(e.getUserJid()));
        topStats.getTopUsersByMemory().forEach(e -> userJids.add(e.getUserJid()));
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        return new ProblemStatsResponse.Builder()
                .stats(stats)
                .topStats(topStats)
                .progress(progress)
                .profilesMap(profilesMap)
                .build();
    }
}
