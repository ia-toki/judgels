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
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.ScoringConfig;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.ExternalScoreboardModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;

public class ContestScoreboardUpdater {
    private final ObjectMapper objectMapper;
    private final ContestTimer contestTimer;
    private final ContestStore contestStore;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestModuleStore moduleStore;
    private final ContestContestantStore contestantStore;
    private final ContestProblemStore problemStore;
    private final SubmissionStore programmingSubmissionStore;
    private final ItemSubmissionStore bundleItemSubmissionStore;
    private final ScoreboardIncrementalMarker scoreboardIncrementalMarker;
    private final ScoreboardProcessorRegistry scoreboardProcessorRegistry;
    private final ContestScoreboardPusher scoreboardPusher;
    private final JophielClient jophielClient;
    private final SandalphonClient sandalphonClient;

    public ContestScoreboardUpdater(
            ObjectMapper objectMapper,
            ContestTimer contestTimer,
            ContestStore contestStore,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            SubmissionStore programmingSubmissionStore,
            ItemSubmissionStore bundleItemSubmissionStore,
            ScoreboardIncrementalMarker scoreboardIncrementalMarker,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            ContestScoreboardPusher scoreboardPusher,
            JophielClient jophielClient,
            SandalphonClient sandalphonClient) {

        this.objectMapper = objectMapper;
        this.contestTimer = contestTimer;
        this.contestStore = contestStore;
        this.scoreboardStore = scoreboardStore;
        this.moduleStore = moduleStore;
        this.contestantStore = contestantStore;
        this.problemStore = problemStore;
        this.programmingSubmissionStore = programmingSubmissionStore;
        this.bundleItemSubmissionStore = bundleItemSubmissionStore;
        this.scoreboardIncrementalMarker = scoreboardIncrementalMarker;
        this.scoreboardProcessorRegistry = scoreboardProcessorRegistry;
        this.scoreboardPusher = scoreboardPusher;
        this.jophielClient = jophielClient;
        this.sandalphonClient = sandalphonClient;
    }

    @UnitOfWork
    public void update(Contest contest) {
        ScoreboardProcessor processor = scoreboardProcessorRegistry.get(contest.getStyle());

        ContestModulesConfig contestModulesConfig = moduleStore.getConfig(contest.getJid(), contest.getStyle());
        StyleModuleConfig styleModuleConfig = moduleStore.getStyleModuleConfig(contest.getJid(), contest.getStyle());

        Optional<String> previousContestJid = contestModulesConfig.getMergedScoreboard()
                .flatMap(c -> c.getPreviousContestJid());

        List<ContestProblem> problems = Lists.newArrayList();
        Map<String, ContestContestant> contestantsMap = new HashMap<>();

        if (previousContestJid.isPresent()) {
            problems.addAll(problemStore.getProblems(previousContestJid.get()));
            for (ContestContestant contestant : contestantStore.getApprovedContestants(previousContestJid.get())) {
                contestantsMap.put(contestant.getUserJid(), contestant);
            }
        }

        problems.addAll(problemStore.getProblems(contest.getJid()));
        for (ContestContestant contestant : contestantStore.getApprovedContestants(contest.getJid())) {
            contestantsMap.put(contestant.getUserJid(), contestant);
        }

        List<String> problemJids = Lists.transform(problems, ContestProblem::getProblemJid);
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        List<String> problemAliases = Lists.transform(problems, ContestProblem::getAlias);
        Optional<List<Integer>> problemPoints = problems.stream().anyMatch(p -> p.getPoints().isPresent())
                ? Optional.of(Lists.transform(problems, p -> p.getPoints().orElse(0)))
                : Optional.empty();

        Set<ContestContestant> contestants = ImmutableSet.copyOf(contestantsMap.values());
        Set<String> contestantJidsSet = contestants.stream().map(ContestContestant::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = jophielClient.getProfiles(contestantJidsSet, contest.getBeginTime());
        Map<String, ScoringConfig> scoringConfigsMap = sandalphonClient.getProgrammingProblemScoringConfigs(problemJidsSet);

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

        List<Submission> programmingSubmissions = Lists.newArrayList();
        List<ItemSubmission> bundleItemSubmissions = Lists.newArrayList();

        if (previousContestJid.isPresent() && lastSubmissionId == 0) {
            programmingSubmissions.addAll(programmingSubmissionStore
                    .getSubmissionsForScoreboard(previousContestJid.get(), withGradingDetails, 0));
            bundleItemSubmissions.addAll(bundleItemSubmissionStore
                    .getSubmissionsForScoreboard(previousContestJid.get()));
        }

        programmingSubmissions.addAll(programmingSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid(), withGradingDetails, lastSubmissionId));
        bundleItemSubmissions.addAll(bundleItemSubmissionStore
                .getSubmissionsForScoreboard(contest.getJid()));

        programmingSubmissions = programmingSubmissions
                .stream()
                .filter(s -> s.getLatestGrading().isPresent())
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .filter(s -> contestantJidsSet.contains(s.getUserJid()))
                .collect(toList());
        bundleItemSubmissions = bundleItemSubmissions
                .stream()
                .filter(s -> s.getGrading().isPresent())
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .filter(s -> contestantJidsSet.contains(s.getUserJid()))
                .collect(toList());

        Map<ContestScoreboardType, Scoreboard> scoreboards = new HashMap<>();
        Map<ContestScoreboardType, ScoreboardIncrementalContent> incrementalContents = new HashMap<>();

        Map<String, Instant> freezeTimesMap = new HashMap<>();
        for (ContestScoreboardType type : new ContestScoreboardType[]{OFFICIAL, FROZEN}) {
            if (type == FROZEN) {
                putFreezeTime(freezeTimesMap, contest);

                if (previousContestJid.isPresent()) {
                    Optional<Contest> previousContest = contestStore.getContestByJid(previousContestJid.get());
                    if (previousContest.isPresent()) {
                        putFreezeTime(freezeTimesMap, previousContest.get());
                    }
                }

                if (freezeTimesMap.isEmpty()) {
                    continue;
                }
            }

            Optional<ScoreboardIncrementalContent> incrementalContent = Optional.ofNullable(
                    incrementalMark.getIncrementalContents().get(type));

            ScoreboardProcessResult result = processor.process(
                    contest,
                    state,
                    incrementalContent,
                    styleModuleConfig,
                    contestants,
                    profilesMap,
                    scoringConfigsMap,
                    programmingSubmissions,
                    bundleItemSubmissions,
                    freezeTimesMap);

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

        if (contestTimer.hasEnded(contest)) {
            for (ScoreboardEntry e : scoreboards.get(OFFICIAL).getContent().getEntries()) {
                if (e.hasSubmission()) {
                    contestantStore.updateContestantFinalRank(contest.getJid(), e.getContestantJid(), e.getRank());
                }
            }
        }

        scoreboardIncrementalMarker.setMark(
                contest.getJid(),
                incrementalMarkKey,
                incrementalMark.getTimestamp(),
                incrementalContents);

        if (contestModulesConfig.getExternalScoreboard().isPresent()) {
            ExternalScoreboardModuleConfig config = contestModulesConfig.getExternalScoreboard().get();
            scoreboardPusher.pushScoreboard(
                    config.getReceiverUrl(),
                    config.getReceiverSecret(),
                    contest.getJid(),
                    contest.getStyle(),
                    scoreboards);
        }
    }

    private void putFreezeTime(Map<String, Instant> freezeTimesMap, Contest contest) {
        ContestModulesConfig config = moduleStore.getConfig(contest.getJid(), contest.getStyle());

        if (config.getFrozenScoreboard().isPresent()) {
            Duration duration = config.getFrozenScoreboard().get().getFreezeDurationBeforeEndTime();
            freezeTimesMap.put(contest.getJid(), contest.getEndTime().minus(duration));
        }
    }
}
