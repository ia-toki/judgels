package judgels.uriel.contest.scoreboard.ioi;

import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;

public final class StandardIoiScoreboardEntryComparator implements IoiScoreboardEntryComparator {
    @Override
    public int compareWithoutTieBreakerForEqualRanks(IoiScoreboardEntry entry1, IoiScoreboardEntry entry2) {
        return Integer.compare(entry2.getTotalScores(), entry1.getTotalScores());
    }
}
