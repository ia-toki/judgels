package judgels.uriel.contest.scoreboard.troc;

import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardEntry;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;

public final class StandardTrocScoreboardEntryComparator implements ScoreboardEntryComparator<TrocScoreboardEntry> {
    @Override
    public int compareWithoutTieBreakerForEqualRanks(TrocScoreboardEntry entry1, TrocScoreboardEntry entry2) {
        if (entry1.getTotalPoints() != entry2.getTotalPoints()) {
            return Integer.compare(entry2.getTotalPoints(), entry1.getTotalPoints());
        }
        return Long.compare(entry1.getTotalPenalties(), entry2.getTotalPenalties());
    }

    @Override
    public int compareWithTieBreakerForEqualRanks(TrocScoreboardEntry entry1, TrocScoreboardEntry entry2) {
        int totalAttempts1 = entry1.getAttemptsList().stream().mapToInt(i -> i).sum();
        int totalAttempts2 = entry2.getAttemptsList().stream().mapToInt(i -> i).sum();

        if (totalAttempts1 != totalAttempts2) {
            return Integer.compare(totalAttempts2, totalAttempts1);
        }
        if (entry1.getContestantRating() != entry2.getContestantRating()) {
            return Integer.compare(entry2.getContestantRating(), entry1.getContestantRating());
        }
        return entry1.getContestantUsername().compareTo(entry2.getContestantUsername());
    }
}
