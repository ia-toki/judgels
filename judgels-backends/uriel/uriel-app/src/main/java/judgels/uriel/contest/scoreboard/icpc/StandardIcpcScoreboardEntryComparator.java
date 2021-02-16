package judgels.uriel.contest.scoreboard.icpc;

import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;

public final class StandardIcpcScoreboardEntryComparator implements ScoreboardEntryComparator<IcpcScoreboardEntry> {
    @Override
    public int compareWithoutTieBreakerForEqualRanks(IcpcScoreboardEntry entry1, IcpcScoreboardEntry entry2) {
        if (entry1.getTotalAccepted() != entry2.getTotalAccepted()) {
            return Integer.compare(entry2.getTotalAccepted(), entry1.getTotalAccepted());
        }

        if (entry1.getTotalPenalties() != entry2.getTotalPenalties()) {
            return Long.compare(entry1.getTotalPenalties(), entry2.getTotalPenalties());
        }

        return Long.compare(entry1.getLastAcceptedPenalty(), entry2.getLastAcceptedPenalty());
    }

    @Override
    public int compareWithTieBreakerForEqualRanks(IcpcScoreboardEntry entry1, IcpcScoreboardEntry entry2) {
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
