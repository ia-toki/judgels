package judgels.uriel.contest.scoreboard.bundle;

import java.time.Instant;
import java.util.Comparator;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardEntry;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;

public final class UsingTotalScoresBundleScoreboardEntryComparator implements
        ScoreboardEntryComparator<BundleScoreboardEntry> {

    @Override
    public int compareWithoutTieBreakerForEqualRanks(BundleScoreboardEntry entry1, BundleScoreboardEntry entry2) {
        return Integer.compare(entry2.getTotalScores(), entry1.getTotalScores());
    }

    @Override
    public int compareWithTieBreakerForEqualRanks(BundleScoreboardEntry entry1, BundleScoreboardEntry entry2) {
        return Comparator.nullsLast(Instant::compareTo)
                .compare(entry2.getLastAnsweredTime().orElse(null), entry1.getLastAnsweredTime().orElse(null));
    }
}
