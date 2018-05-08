package judgels.uriel.contest.scoreboard;

import java.util.Comparator;

public interface ScoreboardEntryComparator<T> extends Comparator<T> {
    int compareWithoutTieBreakerForEqualRanks(T entry1, T entry2);
    int compareWithTieBreakerForEqualRanks(T entry1, T entry2);

    @Override
    default int compare(T entry1, T entry2) {
        int withoutTieBreaker = compareWithoutTieBreakerForEqualRanks(entry1, entry2);

        if (withoutTieBreaker != 0) {
            return withoutTieBreaker;
        }

        return compareWithTieBreakerForEqualRanks(entry1, entry2);
    }
}
