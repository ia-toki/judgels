package judgels.uriel.contest.scoreboard.gcj;

import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardEntry;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;

public final class StandardGcjScoreboardEntryComparator implements ScoreboardEntryComparator<GcjScoreboardEntry> {
    @Override
    public int compareWithoutTieBreakerForEqualRanks(GcjScoreboardEntry entry1, GcjScoreboardEntry entry2) {
        if (entry1.getTotalPoints() != entry2.getTotalPoints()) {
            return Integer.compare(entry2.getTotalPoints(), entry1.getTotalPoints());
        }
        return Long.compare(entry1.getTotalPenalties(), entry2.getTotalPenalties());
    }

    @Override
    public int compareWithTieBreakerForEqualRanks(GcjScoreboardEntry entry1, GcjScoreboardEntry entry2) {
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
