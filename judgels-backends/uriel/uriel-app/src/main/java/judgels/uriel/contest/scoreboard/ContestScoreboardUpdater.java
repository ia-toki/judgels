package judgels.uriel.contest.scoreboard;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.submission.bundle.ContestItemSubmissionStore;

public class ContestScoreboardUpdater {
    private final ObjectMapper objectMapper;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestModuleStore moduleStore;
    private final ContestContestantStore contestantStore;
    private final ContestProblemStore problemStore;
    private final SubmissionStore programmingSubmissionStore;
    private final ContestItemSubmissionStore bundleItemSubmissionStore;
    private final ScoreboardProcessorRegistry scoreboardProcessorRegistry;
    private final Clock clock;

    public ContestScoreboardUpdater(
            ObjectMapper objectMapper,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            SubmissionStore programmingSubmissionStore,
            ContestItemSubmissionStore bundleItemSubmissionStore,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            Clock clock) {

        this.objectMapper = objectMapper;
        this.scoreboardStore = scoreboardStore;
        this.moduleStore = moduleStore;
        this.contestantStore = contestantStore;
        this.problemStore = problemStore;
        this.programmingSubmissionStore = programmingSubmissionStore;
        this.bundleItemSubmissionStore = bundleItemSubmissionStore;
        this.scoreboardProcessorRegistry = scoreboardProcessorRegistry;
        this.clock = clock;
    }

    @UnitOfWork
    public void update(Contest contest) {
        ContestModulesConfig contestModulesConfig = moduleStore.getConfig(contest.getJid(), contest.getStyle());
        StyleModuleConfig styleModuleConfig = moduleStore.getStyleModuleConfig(contest.getJid(), contest.getStyle());

        List<ContestProblem> problems = problemStore.getProblems(contest.getJid());
        List<String> problemJids = problems.stream().map(ContestProblem::getProblemJid).collect(toList());
        List<String> problemAliases = problems.stream().map(ContestProblem::getAlias).collect(toList());
        Optional<List<Integer>> problemPoints = styleModuleConfig.hasPointsPerProblem()
                ? Optional.of(problems.stream().map(p -> p.getPoints().orElse(0)).collect(toList()))
                : Optional.empty();

        Set<ContestContestant> contestants = contestantStore.getApprovedContestants(contest.getJid());
        Set<String> contestantJids = contestants.stream().map(ContestContestant::getUserJid).collect(toSet());
        Map<String, Optional<Instant>> contestantStartTimesMap = contestants.stream().collect(
                Collectors.toMap(ContestContestant::getUserJid, ContestContestant::getContestStartTime));

        ScoreboardState scoreboardState = new ScoreboardState.Builder()
                .problemJids(problemJids)
                .contestantJids(contestantJids)
                .problemPoints(problemPoints)
                .problemAliases(problemAliases)
                .build();

        List<Submission> programmingSubmissions = programmingSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid());
        List<ItemSubmission> bundleItemSubmissions = bundleItemSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid());

        generateAndUpsertScoreboard(
                contest,
                scoreboardState,
                styleModuleConfig,
                contestantStartTimesMap,
                programmingSubmissions,
                bundleItemSubmissions,
                Optional.empty(),
                ContestScoreboardType.OFFICIAL);

        if (contestModulesConfig.getFrozenScoreboard().isPresent()) {
            Duration freezeDuration = contestModulesConfig.getFrozenScoreboard().get().getFreezeDurationBeforeEndTime();
            Instant freezeTime = contest.getEndTime().minus(freezeDuration);

            if (clock.instant().isAfter(freezeTime)) {
                generateAndUpsertScoreboard(
                        contest,
                        scoreboardState,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        programmingSubmissions,
                        bundleItemSubmissions,
                        Optional.of(freezeTime),
                        ContestScoreboardType.FROZEN);
            }
        }
    }

    private void generateAndUpsertScoreboard(
            Contest contest,
            ScoreboardState scoreboardState,
            StyleModuleConfig styleModuleConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime,
            ContestScoreboardType contestScoreboardType) {

        String scoreboard = scoreboardProcessorRegistry.get(contest.getStyle())
                .computeToString(
                        objectMapper,
                        scoreboardState,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        programmingSubmissions,
                        bundleItemSubmissions,
                        freezeTime);

        scoreboardStore.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .scoreboard(scoreboard)
                .type(contestScoreboardType)
                .build());
    }
}
