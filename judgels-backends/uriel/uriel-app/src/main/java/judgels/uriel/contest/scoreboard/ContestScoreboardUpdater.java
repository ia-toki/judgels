package judgels.uriel.contest.scoreboard;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.ExternalScoreboardData;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import org.glassfish.jersey.client.JerseyClientBuilder;

public class ContestScoreboardUpdater {
    private final ObjectMapper objectMapper;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestModuleStore moduleStore;
    private final ContestContestantStore contestantStore;
    private final ContestProblemStore problemStore;
    private final SubmissionStore programmingSubmissionStore;
    private final ItemSubmissionStore bundleItemSubmissionStore;
    private final ScoreboardIncrementalMarker scoreboardIncrementalMarker;
    private final ScoreboardProcessorRegistry scoreboardProcessorRegistry;
    private final Clock clock;

    public ContestScoreboardUpdater(
            ObjectMapper objectMapper,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            SubmissionStore programmingSubmissionStore,
            ItemSubmissionStore bundleItemSubmissionStore,
            ScoreboardIncrementalMarker scoreboardIncrementalMarker,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            Clock clock) {

        this.objectMapper = objectMapper;
        this.scoreboardStore = scoreboardStore;
        this.moduleStore = moduleStore;
        this.contestantStore = contestantStore;
        this.problemStore = problemStore;
        this.programmingSubmissionStore = programmingSubmissionStore;
        this.bundleItemSubmissionStore = bundleItemSubmissionStore;
        this.scoreboardIncrementalMarker = scoreboardIncrementalMarker;
        this.scoreboardProcessorRegistry = scoreboardProcessorRegistry;
        this.clock = clock;
    }

    @UnitOfWork
    public void update(Contest contest) {
        Instant now = clock.instant();

        ScoreboardProcessor processor = scoreboardProcessorRegistry.get(contest.getStyle());

        ContestModulesConfig contestModulesConfig = moduleStore.getConfig(contest.getJid(), contest.getStyle());
        StyleModuleConfig styleModuleConfig = moduleStore.getStyleModuleConfig(contest.getJid(), contest.getStyle());

        List<ContestProblem> problems = problemStore.getProblems(contest.getJid());
        List<String> problemJids = Lists.transform(problems, ContestProblem::getProblemJid);
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        List<String> problemAliases = Lists.transform(problems, ContestProblem::getAlias);
        Optional<List<Integer>> problemPoints = problems.stream().anyMatch(p -> p.getPoints().isPresent())
                ? Optional.of(Lists.transform(problems, p -> p.getPoints().orElse(0)))
                : Optional.empty();

        Set<ContestContestant> contestants = contestantStore.getApprovedContestants(contest.getJid());
        Set<String> contestantJidsSet = contestants.stream().map(ContestContestant::getUserJid).collect(toSet());

        ScoreboardState state = new ScoreboardState.Builder()
                .problemJids(problemJids)
                .problemPoints(problemPoints)
                .problemAliases(problemAliases)
                .build();

        ScoreboardIncrementalMarkKey incrementalMarkKey = new ScoreboardIncrementalMarkKey.Builder()
                .style(contest.getStyle())
                .beginTime(contest.getBeginTime())
                .duration(contest.getDuration())
                .contestants(contestants)
                .problemJids(problemJids)
                .problemPoints(problemPoints)
                .styleModuleConfig(styleModuleConfig)
                .frozenScoreboardModuleConfig(contestModulesConfig.getFrozenScoreboard())
                .build();

        ScoreboardIncrementalMark incrementalMark =
                scoreboardIncrementalMarker.getMark(contest.getJid(), incrementalMarkKey);

        boolean withGradingDetails = processor.requiresGradingDetails(styleModuleConfig);
        long lastSubmissionId = incrementalMark.getLastSubmissionId();
        List<Submission> programmingSubmissions = programmingSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid(), withGradingDetails, lastSubmissionId)
                .stream()
                .filter(s -> s.getLatestGrading().isPresent())
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .filter(s -> contestantJidsSet.contains(s.getUserJid()))
                .collect(toList());
        List<ItemSubmission> bundleItemSubmissions = bundleItemSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid())
                .stream()
                .filter(s -> s.getGrading().isPresent())
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .filter(s -> contestantJidsSet.contains(s.getUserJid()))
                .collect(toList());

        Map<ContestScoreboardType, Scoreboard> scoreboards = new HashMap<>();
        Map<ContestScoreboardType, ScoreboardIncrementalContent> incrementalContents = new HashMap<>();

        updateScoreboard(
                contest,
                processor,
                state,
                Optional.ofNullable(incrementalMark.getIncrementalContents().get(OFFICIAL)),
                styleModuleConfig,
                contestants,
                programmingSubmissions,
                bundleItemSubmissions,
                Optional.empty(),
                OFFICIAL,
                scoreboards,
                incrementalContents);

        if (contestModulesConfig.getFrozenScoreboard().isPresent()) {
            Duration freezeDuration = contestModulesConfig.getFrozenScoreboard().get().getFreezeDurationBeforeEndTime();
            Instant freezeTime = contest.getEndTime().minus(freezeDuration);

            updateScoreboard(
                    contest,
                    processor,
                    state,
                    Optional.ofNullable(incrementalMark.getIncrementalContents().get(FROZEN)),
                    styleModuleConfig,
                    contestants,
                    programmingSubmissions,
                    bundleItemSubmissions,
                    Optional.of(freezeTime),
                    FROZEN,
                    scoreboards,
                    incrementalContents);
        }

        scoreboardIncrementalMarker.setMark(
                contest.getJid(),
                incrementalMarkKey,
                incrementalMark.getTimestamp(),
                incrementalContents);

        if (contestModulesConfig.getExternalScoreboard().isPresent()) {
            ExternalScoreboardData data = new ExternalScoreboardData.Builder()
                    .receiverSecret(contestModulesConfig.getExternalScoreboard().get().getReceiverSecret())
                    .contestJid(contest.getJid())
                    .contestStyle(contest.getStyle())
                    .updatedTime(now)
                    .scoreboards(scoreboards)
                    .build();

            byte[] dataBytes;
            try {
                dataBytes = objectMapper.writeValueAsBytes(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JerseyClientBuilder.createClient()
                    .target(contestModulesConfig.getExternalScoreboard().get().getReceiverUrl())
                    .request()
                    .post(Entity.entity(dataBytes, MediaType.APPLICATION_JSON));
        }
    }

    private void updateScoreboard(
            Contest contest,
            ScoreboardProcessor processor,
            ScoreboardState state,
            Optional<ScoreboardIncrementalContent> incrementalContent,
            StyleModuleConfig styleModuleConfig,
            Set<ContestContestant> contestants,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime,
            ContestScoreboardType type,
            Map<ContestScoreboardType, Scoreboard> scoreboards,
            Map<ContestScoreboardType, ScoreboardIncrementalContent> incrementalContents) {

        ScoreboardProcessResult result = processor.process(
                contest,
                state,
                incrementalContent,
                styleModuleConfig,
                contestants,
                programmingSubmissions,
                bundleItemSubmissions,
                freezeTime);
        Scoreboard scoreboard = processor.create(state, result.getEntries());

        String scoreboardJson;
        try {
            scoreboardJson = objectMapper.writeValueAsString(scoreboard);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

        scoreboardStore.upsertScoreboard(contest.getJid(), new ContestScoreboardData.Builder()
                .scoreboard(scoreboardJson)
                .type(type)
                .build());

        scoreboards.put(type, scoreboard);
        incrementalContents.put(type, result.getIncrementalContent());
    }
}
