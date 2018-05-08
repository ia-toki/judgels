package judgels.uriel.contest.scoreboard.ioi;

import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;

public final class UsingLastAffectingPenaltyIoiScoreboardEntryComparator implements IoiScoreboardEntryComparator {
    @Override
    public int compareWithoutTieBreakerForEqualRanks(IoiScoreboardEntry entry1, IoiScoreboardEntry entry2) {
        if (entry1.getTotalScores() != entry2.getTotalScores()) {
            return Integer.compare(entry2.getTotalScores(), entry1.getTotalScores());
        }

        return Long.compare(entry1.getLastAffectingPenalty(), entry2.getLastAffectingPenalty());
    }
}
