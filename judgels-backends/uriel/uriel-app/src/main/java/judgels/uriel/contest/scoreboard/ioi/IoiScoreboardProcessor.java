package judgels.uriel.contest.scoreboard.ioi;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.gabriel.api.Verdict;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
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
    public List<IoiScoreboardEntry> computeEntries(
            ScoreboardState scoreboardState,
            Contest contest,
            StyleModuleConfig styleModuleConfig,
            Set<ContestContestant> contestants,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime) {

        IoiStyleModuleConfig ioiStyleModuleConfig = (IoiStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        Set<String> contestantJids = contestants.stream().map(ContestContestant::getUserJid).collect(toSet());
        Map<String, Optional<Instant>> contestantStartTimesMap = contestants.stream()
                .collect(toMap(ContestContestant::getUserJid, ContestContestant::getContestStartTime));

        List<Submission> filteredSubmissions = programmingSubmissions.stream()
                .filter(s -> contestantJids.contains(s.getUserJid()))
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .collect(Collectors.toList());

        Map<String, List<Submission>> submissionsMap = new HashMap<>();
        contestantJids.forEach(c -> submissionsMap.put(c, new ArrayList<>()));
        filteredSubmissions.forEach(s -> {
            submissionsMap.get(s.getUserJid()).add(s);
        });

        List<IoiScoreboardEntry> entries = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            long lastAffectingPenalty = 0;
            Map<String, Optional<Integer>> scoresMap = new HashMap<>();
            problemJids.forEach(p -> scoresMap.put(p, Optional.empty()));

            for (Submission submission : submissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();

                if (!contestantJids.contains(submission.getUserJid())) {
                    continue;
                }
                if (!problemJidsSet.contains(problemJid)) {
                    continue;
                }
                if (!submission.getLatestGrading().isPresent()) {
                    continue;
                }

                Grading grading = submission.getLatestGrading().get();
                Verdict verdict = grading.getVerdict();
                if (verdict.equals(Verdict.PENDING)) {
                    continue;
                }

                int score = grading.getScore();
                if (scoresMap.get(problemJid).isPresent() && score < scoresMap.get(problemJid).get()) {
                    continue;
                }
                scoresMap.put(submission.getProblemJid(), Optional.of(score));

                if (score > 0) {
                    lastAffectingPenalty = computeLastAffectingPenalty(
                            submission.getTime(),
                            contestantStartTimesMap.get(contestantJid),
                            contest.getBeginTime());
                }
            }

            entries.add(new IoiScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .scores(problemJids
                            .stream()
                            .map(scoresMap::get)
                            .collect(Collectors.toList()))
                    .totalScores(scoresMap.values().stream().mapToInt(s -> s.orElse(0)).sum())
                    .lastAffectingPenalty(lastAffectingPenalty)
                    .build());
        }

        IoiScoreboardEntryComparator comparator;
        if (ioiStyleModuleConfig.getUsingLastAffectingPenalty()) {
            comparator = new UsingLastAffectingPenaltyIoiScoreboardEntryComparator();
        } else {
            comparator = new StandardIoiScoreboardEntryComparator();
        }

        return sortEntriesAndAssignRanks(comparator, entries);
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

        IoiScoreboardEntryComparator comparator;
        if (config.getUsingLastAffectingPenalty()) {
            comparator = new UsingLastAffectingPenaltyIoiScoreboardEntryComparator();
        } else {
            comparator = new StandardIoiScoreboardEntryComparator();
        }

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

        newEntries = sortEntriesAndAssignRanks(comparator, newEntries);

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

    private Long computeLastAffectingPenalty(
            Instant submissionTime,
            Optional<Instant> contestantStartTime,
            Instant contestBeginTime) {

        return submissionTime.toEpochMilli() - contestantStartTime.orElse(contestBeginTime).toEpochMilli();
    }
}
