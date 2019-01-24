package judgels.uriel.contest.scoreboard.icpc;

import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;

public final class StandardIcpcScoreboardEntryComparator implements
        ScoreboardEntryComparator<IcpcScoreboardEntry> {
    @Override
    public int compareWithoutTieBreakerForEqualRanks(IcpcScoreboardEntry entry1, IcpcScoreboardEntry entry2) {
        return Integer.compare(entry2.getTotalAccepted(), entry1.getTotalAccepted());
    }

    @Override
    public int compareWithTieBreakerForEqualRanks(IcpcScoreboardEntry entry1, IcpcScoreboardEntry entry2) {
        return Long.compare(entry1.getTotalPenalties(), entry2.getTotalPenalties());
    }
}
