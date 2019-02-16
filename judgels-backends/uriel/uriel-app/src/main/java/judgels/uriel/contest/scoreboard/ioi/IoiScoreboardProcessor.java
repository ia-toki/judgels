package judgels.uriel.contest.scoreboard.ioi;

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
import judgels.gabriel.api.Verdicts;
import judgels.sandalphon.api.submission.Grading;
import judgels.sandalphon.api.submission.programming.ProgrammingSubmission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class IoiScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public Scoreboard parseFromString(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, IoiScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IoiScoreboardEntry> getEntries(Scoreboard scoreboard) {
        return ((IoiScoreboard) scoreboard).getContent().getEntries();
    }

    @Override
    public Scoreboard replaceEntries(Scoreboard scoreboard, List<?> entries) {
        IoiScoreboard ioiScoreboard = (IoiScoreboard) scoreboard;
        return new IoiScoreboard.Builder()
                .state(ioiScoreboard.getState())
                .content(new IoiScoreboardContent.Builder()
                        .entries((List<? extends IoiScoreboardEntry>) entries)
                        .build())
                .build();
    }

    @Override
    public int getTotalEntries(Scoreboard scoreboard) {
        return ((IoiScoreboard) scoreboard).getContent().getEntries().size();
    }

    @Override
    public String computeToString(
            ObjectMapper mapper,
            ScoreboardState scoreboardState,
            Contest contest,
            StyleModuleConfig styleModuleConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<ProgrammingSubmission> submissions,
            Optional<Instant> freezeTime) {
        IoiStyleModuleConfig ioiStyleModuleConfig = (IoiStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        Set<String> contestantJids = scoreboardState.getContestantJids();

        List<ProgrammingSubmission> filteredSubmissions = submissions.stream()
                .filter(s -> contestantJids.contains(s.getUserJid()))
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .collect(Collectors.toList());

        Map<String, List<ProgrammingSubmission>> submissionsMap = new HashMap<>();
        contestantJids.forEach(c -> submissionsMap.put(c, new ArrayList<>()));
        filteredSubmissions.forEach(s -> {
            submissionsMap.get(s.getUserJid()).add(s);
        });

        List<IoiScoreboardEntry> entries = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            long lastAffectingPenalty = 0;
            Map<String, Optional<Integer>> scoresMap = new HashMap<>();
            problemJids.forEach(p -> scoresMap.put(p, Optional.empty()));

            for (ProgrammingSubmission submission : submissionsMap.get(contestantJid)) {
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
                if (verdict.equals(Verdicts.PENDING)) {
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

        entries = sortEntriesAndAssignRanks(comparator, entries);

        try {
            return mapper.writeValueAsString(new IoiScoreboard.Builder()
                    .state(scoreboardState)
                    .content(new IoiScoreboardContent.Builder()
                            .entries(entries)
                            .build())
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids) {
        IoiScoreboard ioiScoreboard = (IoiScoreboard) scoreboard;

        Set<String> filteredContestantJids = ioiScoreboard.getState().getContestantJids()
                .stream()
                .filter(contestantJids::contains)
                .collect(Collectors.toSet());

        ScoreboardState filteredState = new ScoreboardState.Builder()
                .from(ioiScoreboard.getState())
                .contestantJids(filteredContestantJids)
                .build();

        List<IoiScoreboardEntry> filteredEntries = ioiScoreboard.getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new IoiScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new IoiScoreboard.Builder()
                .state(filteredState)
                .content(new IoiScoreboardContent.Builder().entries(filteredEntries).build())
                .build();
    }

    public Scoreboard filterProblemJids(
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
