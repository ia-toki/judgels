package judgels.uriel.contest.scoreboard.gcj;

import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardEntry;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;

public final class StandardGcjScoreboardEntryComparator implements
        ScoreboardEntryComparator<GcjScoreboardEntry> {
    @Override
    public int compareWithoutTieBreakerForEqualRanks(GcjScoreboardEntry entry1, GcjScoreboardEntry entry2) {
        return Integer.compare(entry2.getTotalPoints(), entry1.getTotalPoints());
    }

    @Override
    public int compareWithTieBreakerForEqualRanks(GcjScoreboardEntry entry1, GcjScoreboardEntry entry2) {
        return Long.compare(entry1.getTotalPenalties(), entry2.getTotalPenalties());
    }
}
