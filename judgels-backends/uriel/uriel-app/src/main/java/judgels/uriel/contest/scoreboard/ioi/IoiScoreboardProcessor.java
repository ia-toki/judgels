package judgels.uriel.contest.scoreboard.ioi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.sandalphon.api.submission.Grading;
import judgels.sandalphon.api.submission.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
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
    public String computeToString(
            ObjectMapper mapper,
            ScoreboardState scoreboardState,
            Contest contest,
            ContestModulesConfig contestModulesConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<Submission> submissionList) {
        if (!contestModulesConfig.getIoiStyle().isPresent()) {
            throw new RuntimeException("IOI-style contest given but IoiStyleConfig not found");
        }
        IoiStyleModuleConfig ioiStyleModuleConfig = contestModulesConfig.getIoiStyle().get();

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = new HashSet<>(problemJids);
        Set<String> contestantJids = scoreboardState.getContestantJids();

        Map<String, List<Submission>> submissionsMap = new HashMap<>();
        contestantJids.forEach(c -> submissionsMap.put(c, new ArrayList<>()));
        List<Submission> mutableSubmissionList = new ArrayList<>(submissionList);
        mutableSubmissionList.forEach(s -> {
            submissionsMap.putIfAbsent(s.getUserJid(), new ArrayList<>());
            submissionsMap.get(s.getUserJid()).add(s);
        });

        List<IoiScoreboardEntry> scoreboardEntryList = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            List<Submission> submissions = submissionsMap.get(contestantJid);
            long lastAffectingPenalty = 0;
            Map<String, Optional<Integer>> scoresMap = new HashMap<>();
            problemJids.forEach(p -> scoresMap.put(p, Optional.empty()));

            for (Submission submission : submissions) {
                if (!contestantJids.contains(submission.getUserJid())) {
                    continue;
                }

                if (!problemJidsSet.contains(submission.getProblemJid())) {
                    continue;
                }

                if (!submission.getLatestGrading().isPresent()
                        || submission.getLatestGrading().get().getVerdict().equals(Grading.PENDING)) {
                    continue;
                }

                Grading grading = submission.getLatestGrading().get();
                if (!scoresMap.get(submission.getProblemJid()).isPresent()
                        || scoresMap.get(submission.getProblemJid()).get() < grading.getScore()) {
                    scoresMap.put(submission.getProblemJid(), Optional.of(grading.getScore()));
                    lastAffectingPenalty = computeLastAffectingPenalty(
                            submission.getTime(),
                            contestantStartTimesMap.get(contestantJid),
                            contest.getBeginTime());
                }
            }

            scoreboardEntryList.add(new IoiScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .addAllScores(problemJids
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

        scoreboardEntryList = sortEntriesAndAssignRanks(comparator, scoreboardEntryList);

        try {
            return mapper.writeValueAsString(new IoiScoreboard.Builder()
                    .state(scoreboardState)
                    .content(new IoiScoreboardContent.Builder()
                            .addAllEntries(scoreboardEntryList)
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
