package judgels.uriel.contest.scoreboard.ioi;

import static java.util.Collections.emptyMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.gabriel.api.ScoringConfig;
import judgels.gabriel.api.ScoringRoundingMode;
import judgels.gabriel.api.SubtaskResult;
import judgels.gabriel.api.Verdict;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalContent;
import judgels.uriel.contest.scoreboard.ScoreboardProcessResult;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class IoiScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public IoiScoreboard parse(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, IoiScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IoiScoreboard create(ScoreboardState state, List<? extends ScoreboardEntry> entries) {
        return new IoiScoreboard.Builder()
                .state(state)
                .content(new IoiScoreboardContent.Builder()
                        .entries(Lists.transform(entries, e -> (IoiScoreboardEntry) e))
                        .build())
                .build();
    }

    @Override
    public boolean requiresGradingDetails(StyleModuleConfig styleModuleConfig) {
        IoiStyleModuleConfig ioiStyleModuleConfig = (IoiStyleModuleConfig) styleModuleConfig;
        return ioiStyleModuleConfig.getUsingMaxScorePerSubtask();
    }

    @Override
    public ScoreboardProcessResult process(
            ScoreboardState scoreboardState,
            Optional<ScoreboardIncrementalContent> incrementalContent,
            StyleModuleConfig styleModuleConfig,
            Map<String, Set<ContestContestant>> contestContestantsMap,
            Map<String, Instant> contestBeginTimesMap,
            Map<String, Instant> contestFreezeTimesMap,
            Map<String, ScoringConfig> problemScoringConfigsMap,
            Map<String, Profile> profilesMap,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions) {

        IoiStyleModuleConfig ioiStyleModuleConfig = (IoiStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();

        Set<String> contestantJids = new HashSet<>();
        Map<String, Map<String, Optional<Instant>>> contestantStartTimesMap = new HashMap<>();
        for (var entry : contestContestantsMap.entrySet()) {
            contestantStartTimesMap.put(entry.getKey(), new HashMap<>());
            for (ContestContestant contestant : entry.getValue()) {
                contestantJids.add(contestant.getUserJid());
                contestantStartTimesMap.get(entry.getKey()).put(contestant.getUserJid(), contestant.getContestStartTime());
            }
        }

        Map<String, List<Submission>> submissionsMap = new HashMap<>();
        contestantJids.forEach(c -> submissionsMap.put(c, new ArrayList<>()));
        programmingSubmissions.forEach(s -> submissionsMap.get(s.getUserJid()).add(s));

        Optional<IoiScoreboardIncrementalContent> maybeIncrementalContent =
                incrementalContent.map(content -> (IoiScoreboardIncrementalContent) content);

        Map<String, Map<String, Long>> lastAffectingPenaltiesMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getLastAffectingPenaltiesMapsByContestantJid()).orElse(emptyMap()));
        Map<String, Map<String, Optional<Integer>>> scoresMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getScoresMapsByContestantJid()).orElse(emptyMap()));
        Map<String, Map<String, Map<Integer, Double>>> maxScorePerSubtaskMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getMaxScorePerSubtaskMapsByContestantJid()).orElse(emptyMap()));

        Optional<Long> nextLastSubmissionId = maybeIncrementalContent.flatMap(ic -> ic.getLastSubmissionId());
        for (Submission s : programmingSubmissions) {
            if (!s.getLatestGrading().isPresent() || s.getLatestGrading().get().getVerdict() == Verdict.PENDING) {
                break;
            }
            nextLastSubmissionId = Optional.of(s.getId());
        }

        List<IoiScoreboardEntry> entries = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            Map<String, Long> lastAffectingPenaltiesMap = new HashMap<>(
                    lastAffectingPenaltiesMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));
            Map<String, Optional<Integer>> scoresMap = new HashMap<>(
                    scoresMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));
            Map<String, Map<Integer, Double>> maxScorePerSubtaskMap = new HashMap<>(
                    maxScorePerSubtaskMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));

            Profile contestantProfile = profilesMap.get(contestantJid);
            Optional<UserRating> maybeUserRating = contestantProfile.getRating();
            UserRating userRating = maybeUserRating.orElseGet(
                    () -> new UserRating.Builder().publicRating(0).hiddenRating(0).build());

            problemJids.forEach(p -> {
                scoresMap.putIfAbsent(p, Optional.empty());
                maxScorePerSubtaskMap.putIfAbsent(p, emptyMap());
            });

            for (Submission submission : submissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();

                Grading grading = submission.getLatestGrading().get();
                Verdict verdict = grading.getVerdict();
                if (verdict.equals(Verdict.PENDING)) {
                    continue;
                }

                int score;

                if (ioiStyleModuleConfig.getUsingMaxScorePerSubtask()) {
                    Map<Integer, Double> newMaxScorePerSubtask = Maps.newHashMap(
                            maxScorePerSubtaskMap.get(problemJid));

                    for (SubtaskResult subtask : grading.getDetails().get().getSubtaskResults()) {
                        double maxScore = Math.max(
                                subtask.getScore(),
                                newMaxScorePerSubtask.getOrDefault(subtask.getId(), 0.0));

                        newMaxScorePerSubtask.put(subtask.getId(), maxScore);
                    }

                    double newScore = 0;
                    for (double points : newMaxScorePerSubtask.values()) {
                        newScore += points;
                    }

                    maxScorePerSubtaskMap.put(problemJid, newMaxScorePerSubtask);

                    if (problemScoringConfigsMap.get(problemJid).getRoundingMode() == ScoringRoundingMode.ROUND) {
                        score = (int) Math.round(newScore);
                    } else {
                        score = (int) Math.floor(newScore);
                    }
                } else {
                    score = grading.getScore();
                }

                if (submission.getTime().isBefore(contestFreezeTimesMap.getOrDefault(submission.getContainerJid(), Instant.MAX))) {
                    if (!scoresMap.get(problemJid).isPresent() || score > scoresMap.get(problemJid).get()) {
                        scoresMap.put(problemJid, Optional.of(score));

                        if (score > 0) {
                            long lastAffectingPenalty = computeLastAffectingPenalty(
                                    submission.getTime(),
                                    contestantStartTimesMap.get(submission.getContainerJid()).get(contestantJid),
                                    contestBeginTimesMap.get(submission.getContainerJid()));

                            lastAffectingPenaltiesMap.put(submission.getContainerJid(), lastAffectingPenalty);
                        }
                    }
                }

                if (submission.getId() <= nextLastSubmissionId.orElse(Long.MAX_VALUE)) {
                    lastAffectingPenaltiesMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(lastAffectingPenaltiesMap));
                    scoresMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(scoresMap));
                    maxScorePerSubtaskMapsByContestantJid
                            .put(contestantJid, ImmutableMap.copyOf(maxScorePerSubtaskMap));
                }
            }

            entries.add(new IoiScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .contestantUsername(contestantProfile.getUsername())
                    .contestantRating(userRating.getPublicRating())
                    .scores(problemJids
                            .stream()
                            .map(scoresMap::get)
                            .collect(Collectors.toList()))
                    .totalScores(scoresMap.values().stream().mapToInt(s -> s.orElse(0)).sum())
                    .lastAffectingPenalty(lastAffectingPenaltiesMap.values().stream().mapToLong(Long::longValue).sum())
                    .build());
        }

        entries = sortEntriesAndAssignRanks(getComparator(ioiStyleModuleConfig), entries);

        return new ScoreboardProcessResult.Builder()
               .entries(entries)
                .incrementalContent(new IoiScoreboardIncrementalContent.Builder()
                        .lastSubmissionId(nextLastSubmissionId)
                        .lastAffectingPenaltiesMapsByContestantJid(lastAffectingPenaltiesMapsByContestantJid)
                        .scoresMapsByContestantJid(scoresMapsByContestantJid)
                        .maxScorePerSubtaskMapsByContestantJid(maxScorePerSubtaskMapsByContestantJid)
                        .build())
                .build();
    }

    @Override
    public IoiScoreboardEntry clearEntryRank(ScoreboardEntry entry) {
        return new IoiScoreboardEntry.Builder()
                .from((IoiScoreboardEntry) entry)
                .rank(-1)
                .build();
    }

    public IoiScoreboard filterProblemJids(
            IoiScoreboard scoreboard,
            Set<String> problemJids,
            IoiStyleModuleConfig config) {

        ScoreboardState state = scoreboard.getState();
        IoiScoreboardContent content = scoreboard.getContent();

        if (state.getProblemJids().size() == problemJids.size()) {
            return scoreboard;
        }

        ImmutableList.Builder<Integer> openProblemIndicesBuilder = ImmutableList.builder();

        for (int i = 0; i < state.getProblemJids().size(); i++) {
            if (problemJids.contains(state.getProblemJids().get(i))) {
                openProblemIndicesBuilder.add(i);
            }
        }

        List<Integer> openProblemIndices = openProblemIndicesBuilder.build();

        ScoreboardState newState = new ScoreboardState.Builder()
                .from(state)
                .problemJids(filterIndices(state.getProblemJids(), openProblemIndices))
                .problemAliases(filterIndices(state.getProblemAliases(), openProblemIndices))
                .build();

        List<IoiScoreboardEntry> newEntries = Lists.newArrayList();

        for (IoiScoreboardEntry entry : content.getEntries()) {
            List<Optional<Integer>> newScores = filterIndices(entry.getScores(), openProblemIndices);
            int newTotalScores = newScores.stream().filter(Optional::isPresent).mapToInt(Optional::get).sum();

            IoiScoreboardEntry newEntry = new IoiScoreboardEntry.Builder()
                    .from(entry)
                    .scores(newScores)
                    .totalScores(newTotalScores)
                    .build();
            newEntries.add(newEntry);
        }

        newEntries = sortEntriesAndAssignRanks(getComparator(config), newEntries);

        return new IoiScoreboard.Builder()
                .state(newState)
                .content(new IoiScoreboardContent.Builder()
                        .entries(newEntries)
                        .build())
                .build();
    }

    private static <T> List<T> filterIndices(List<T> list, List<Integer> indices) {
        return indices.stream().map(list::get).collect(Collectors.toList());
    }

    private static List<IoiScoreboardEntry> sortEntriesAndAssignRanks(
            IoiScoreboardEntryComparator comparator,
            List<IoiScoreboardEntry> entries) {

        entries.sort(comparator);

        ImmutableList.Builder<IoiScoreboardEntry> newEntries = ImmutableList.builder();

        int previousRank = 0;
        for (int i = 0; i < entries.size(); i++) {
            int assignedRank;
            if (i == 0 || comparator.compareWithoutTieBreakerForEqualRanks(entries.get(i), entries.get(i - 1)) != 0) {
                assignedRank = i + 1;
            } else {
                assignedRank = previousRank;
            }
            previousRank = assignedRank;

            newEntries.add(new IoiScoreboardEntry.Builder().from(entries.get(i)).rank(assignedRank).build());
        }

        return newEntries.build();
    }

    private static long computeLastAffectingPenalty(
            Instant submissionTime,
            Optional<Instant> contestantStartTime,
            Instant contestBeginTime) {

        return submissionTime.toEpochMilli() - contestantStartTime.orElse(contestBeginTime).toEpochMilli();
    }

    private static IoiScoreboardEntryComparator getComparator(IoiStyleModuleConfig config) {
        if (config.getUsingLastAffectingPenalty()) {
            return new UsingLastAffectingPenaltyIoiScoreboardEntryComparator();
        } else {
            return new StandardIoiScoreboardEntryComparator();
        }
    }
}
