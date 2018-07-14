package judgels.uriel.contest.scoreboard.ioi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;
import judgels.uriel.contest.style.IoiContestStyleConfig;

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
            IoiContestStyleConfig config) {

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
}
