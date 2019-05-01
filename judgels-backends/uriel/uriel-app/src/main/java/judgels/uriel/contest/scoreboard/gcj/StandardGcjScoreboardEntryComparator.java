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

        if (totalAttempts1 == 0 && totalAttempts2 == 0) {
            return entry1.getContestantJid().compareTo(entry2.getContestantJid());
        } else if (totalAttempts1 == 0) {
            return 1;
        } else if (totalAttempts2 == 0) {
            return -1;
        } else {
            return Integer.compare(totalAttempts2, totalAttempts1);
        }
    }
}
