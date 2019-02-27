package judgels.uriel.contest.scoreboard;

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
import javax.inject.Named;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.bundle.ProblemWorksheet;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
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
    private final ClientProblemService clientProblemService;
    private final BasicAuthHeader sandalphonClientAuthHeader;
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
            ClientProblemService clientProblemService,
            @Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader,
            Clock clock) {

        this.objectMapper = objectMapper;
        this.scoreboardStore = scoreboardStore;
        this.moduleStore = moduleStore;
        this.contestantStore = contestantStore;
        this.problemStore = problemStore;
        this.programmingSubmissionStore = programmingSubmissionStore;
        this.bundleItemSubmissionStore = bundleItemSubmissionStore;
        this.scoreboardProcessorRegistry = scoreboardProcessorRegistry;
        this.clientProblemService = clientProblemService;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
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

        List<Optional<Integer>> problemItems = problemJids.stream()
                .map(problemJid -> {
                    ProblemInfo problemInfo = clientProblemService.getProblem(sandalphonClientAuthHeader, problemJid);
                    if (problemInfo.getType() != ProblemType.BUNDLE) {
                        return null;
                    }
                    ProblemWorksheet worksheet = clientProblemService.getBundleProblemWorksheet(
                            sandalphonClientAuthHeader,
                            problemJid,
                            Optional.empty());
                    return worksheet.getItems().size();
                })
                .map(Optional::ofNullable)
                .collect(Collectors.toList());

        Map<String, Integer> problemPoints = problemStore.getProblemPointsByJids(
                contest.getJid(),
                ImmutableSet.copyOf(problemJids));

        ScoreboardState scoreboardState = new ScoreboardState.Builder()
                .problemJids(problemJids)
                .contestantJids(contestantJids)
                .problemItems(problemItems)
                .problemPoints(styleModuleConfig.hasPointsPerProblem()
                        ? Optional.of(problemJids.stream().map(problemPoints::get).collect(Collectors.toList()))
                        : Optional.empty())
                .problemAliases(problemJids.stream().map(problemAliasesMap::get).collect(Collectors.toList()))
                .build();

        Map<String, Optional<Instant>> contestantStartTimesMap =
                contestantStore.getApprovedContestantStartTimes(contest.getJid());

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
                Optional.empty(), ContestScoreboardType.OFFICIAL);

        if (contestModulesConfig.getFrozenScoreboard().isPresent()) {
            Duration freezeDuration = contestModulesConfig.getFrozenScoreboard().get()
                    .getFreezeDurationBeforeEndTime();
            Instant freezeTime = contest.getEndTime().minus(freezeDuration);

            if (clock.instant().isAfter(freezeTime)) {
                generateAndUpsertScoreboard(
                        contest,
                        scoreboardState,
                        styleModuleConfig,
                        contestantStartTimesMap,
                        programmingSubmissions,
                        bundleItemSubmissions,
                        Optional.of(freezeTime), ContestScoreboardType.FROZEN);
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
