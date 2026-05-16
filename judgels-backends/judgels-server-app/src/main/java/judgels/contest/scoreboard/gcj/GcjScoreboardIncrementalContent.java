package judgels.contest.scoreboard.gcj;

import java.util.Map;
import judgels.api.contest.scoreboard.GcjScoreboard.GcjScoreboardProblemState;
import judgels.contest.scoreboard.ScoreboardIncrementalContent;
import org.immutables.value.Value;

@Value.Immutable
public interface GcjScoreboardIncrementalContent extends ScoreboardIncrementalContent {
    Map<String, Map<String, Integer>> getAttemptsMapsByContestantJid();
    Map<String, Map<String, Long>> getPenaltyMapsByContestantJid();
    Map<String, Map<String, GcjScoreboardProblemState>> getProblemStateMapsByContestantJid();

    class Builder extends ImmutableGcjScoreboardIncrementalContent.Builder {}
}
