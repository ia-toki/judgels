package judgels.uriel.contest.scoreboard.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.sandalphon.api.submission.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.scoreboard.ScoreboardProcessorRegistry;
import judgels.uriel.contest.submission.ContestSubmissionStore;

public class ContestScoreboardUpdater {
    private final ObjectMapper objectMapper;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestModuleStore moduleStore;
    private final ContestContestantStore contestantStore;
    private final ContestProblemStore problemStore;
    private final ContestSubmissionStore submissionStore;
    private final ScoreboardProcessorRegistry scoreboardProcessorRegistry;
    private final Clock clock;

    public ContestScoreboardUpdater(
            ObjectMapper objectMapper,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            ContestSubmissionStore submissionStore,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            Clock clock) {
        this.objectMapper = objectMapper;
        this.scoreboardStore = scoreboardStore;
        this.moduleStore = moduleStore;
        this.contestantStore = contestantStore;
        this.problemStore = problemStore;
        this.submissionStore = submissionStore;
        this.scoreboardProcessorRegistry = scoreboardProcessorRegistry;
        this.clock = clock;
    }

    @UnitOfWork
    public void update(Contest contest) {
        ContestModulesConfig contestModulesConfig = moduleStore.getConfig(contest.getJid(), contest.getStyle());
        StyleModuleConfig styleModuleConfig = moduleStore.getStyleModuleConfig(contest.getJid(), contest.getStyle());

        List<String> problemJids = problemStore.getProblemJids(contest.getJid());
        Set<String> contestantJids = contestantStore.getApprovedContestantJids(contest.getJid());
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(
                contest.getJid(),
                ImmutableSet.copyOf(problemJids));
        ScoreboardState scoreboardState = new ScoreboardState.Builder()
                .problemJids(problemJids)
                .contestantJids(contestantJids)
                .problemAliases(problemJids.stream().map(problemAliasesMap::get).collect(Collectors.toList()))
                .build();

        Map<String, Optional<Instant>> contestantStartTimesMap =
                contestantStore.getApprovedContestantStartTimes(contest.getJid());

        List<Submission> submissions = submissionStore.getSubmissions(
                contest.getJid(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty())
                .getPage();
        generateAndUpsertScoreboard(
                contest,
                scoreboardState,
                styleModuleConfig,
                contestantStartTimesMap,
                submissions,
                ContestScoreboardType.OFFICIAL);

        if (contestModulesConfig.getFrozenScoreboard().isPresent()) {
            Duration freezeDuration = contestModulesConfig.getFrozenScoreboard().get()
                    .getFreezeDurationBeforeEndTime();
            Instant freezeTime = contest.getEndTime().minus(freezeDuration);
            if (clock.instant().isAfter(freezeTime)) {
                submissions = submissions.stream()
                        .filter(s -> s.getTime().isBefore(freezeTime))
                        .collect(Collectors.toList());
                generateAndUpsertScoreboard(
                        contest,
                        scoreboardState,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions,
                        ContestScoreboardType.FROZEN);
            }
        }
    }

    private void generateAndUpsertScoreboard(
            Contest contest,
            ScoreboardState scoreboardState,
            StyleModuleConfig styleModuleConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<Submission> submissions,
            ContestScoreboardType contestScoreboardType) {
        String scoreboard = scoreboardProcessorRegistry.get(contest.getStyle())
                .computeToString(
                        objectMapper,
                        scoreboardState,
                        contest,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        submissions);

        scoreboardStore.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .scoreboard(scoreboard)
                .type(contestScoreboardType)
                .build());
    }
}
