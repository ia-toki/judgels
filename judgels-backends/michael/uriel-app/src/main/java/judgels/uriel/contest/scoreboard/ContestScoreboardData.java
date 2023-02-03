package judgels.uriel.contest.scoreboard;

import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import org.immutables.value.Value;

@Value.Immutable
public interface ContestScoreboardData {
    ContestScoreboardType getType();
    String getScoreboard();

    class Builder extends ImmutableContestScoreboardData.Builder {}
}
