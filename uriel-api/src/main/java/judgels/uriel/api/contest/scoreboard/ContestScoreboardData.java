package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboardData.class)
public interface ContestScoreboardData {
    ContestScoreboardType getType();
    String getScoreboard();

    class Builder extends ImmutableContestScoreboardData.Builder {}
}
