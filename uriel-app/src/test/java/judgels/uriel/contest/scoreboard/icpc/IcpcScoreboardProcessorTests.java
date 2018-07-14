package judgels.uriel.contest.scoreboard.icpc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import org.junit.jupiter.api.Test;

class IcpcScoreboardProcessorTests {
    private IcpcScoreboardProcessor scoreboardProcessor = new IcpcScoreboardProcessor();

    @Test
    void filter_contestant_jids() {
        IcpcScoreboardEntry entry = new IcpcScoreboardEntry.Builder()
                .rank(0)
                .contestantJid("123")
                .totalAccepted(5)
                .totalPenalties(12)
                .lastAcceptedPenalty(123)
                .build();

        IcpcScoreboard scoreboard = new IcpcScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c2", "c3", "c4")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new IcpcScoreboardContent.Builder()
                        .addEntries(
                                new IcpcScoreboardEntry.Builder().from(entry).rank(1).contestantJid("c1").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(2).contestantJid("c2").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(3).contestantJid("c3").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(4).contestantJid("c4").build())
                        .build())
                .build();

        IcpcScoreboard filteredScoreboard = new IcpcScoreboard.Builder()
                .state(new ScoreboardState.Builder()
                        .addContestantJids("c1", "c3")
                        .addProblemJids("p1", "p2")
                        .addProblemAliases("A", "B")
                        .build())
                .content(new IcpcScoreboardContent.Builder()
                        .addEntries(
                                new IcpcScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c1").build(),
                                new IcpcScoreboardEntry.Builder().from(entry).rank(-1).contestantJid("c3").build())
                        .build())
                .build();

        assertThat(scoreboardProcessor.filterContestantJids(scoreboard, ImmutableSet.of("c1", "c3")))
                .isEqualTo(filteredScoreboard);
    }
}
