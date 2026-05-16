package judgels.contest.scoreboard;

import judgels.api.contest.scoreboard.ContestScoreboardType;
import org.immutables.value.Value;

@Value.Immutable
public interface ContestScoreboardData {
    ContestScoreboardType getType();
    String getScoreboard();

    class Builder extends ImmutableContestScoreboardData.Builder {}
}
