package judgels.uriel.contest.scoreboard;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
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

        ScoreboardState state = new ScoreboardState.Builder()
                .problemJids(problemJids)
                .problemPoints(problemPoints)
                .problemAliases(problemAliases)
                .build();

        List<Submission> programmingSubmissions = programmingSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid());
        List<ItemSubmission> bundleItemSubmissions = bundleItemSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid());

        generateAndUpsertScoreboard(
                contest,
                state,
                styleModuleConfig,
                contestants,
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
                        state,
                        styleModuleConfig,
                        contestants,
                        programmingSubmissions,
                        bundleItemSubmissions,
                        Optional.of(freezeTime),
                        ContestScoreboardType.FROZEN);
            }
        }
    }

    private void generateAndUpsertScoreboard(
            Contest contest,
            ScoreboardState state,
            StyleModuleConfig styleModuleConfig,
            Set<ContestContestant> contestants,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime,
            ContestScoreboardType contestScoreboardType) {

        ScoreboardProcessor processor = scoreboardProcessorRegistry.get(contest.getStyle());

        List<? extends ScoreboardEntry> entries = processor.computeEntries(
                state,
                contest,
                styleModuleConfig,
                contestants,
                programmingSubmissions,
                bundleItemSubmissions,
                freezeTime);
        Scoreboard scoreboard = processor.create(state, entries);

        String scoreboardJson;
        try {
            scoreboardJson = objectMapper.writeValueAsString(scoreboard);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

        scoreboardStore.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .scoreboard(scoreboardJson)
                .type(contestScoreboardType)
                .build());
    }
}
