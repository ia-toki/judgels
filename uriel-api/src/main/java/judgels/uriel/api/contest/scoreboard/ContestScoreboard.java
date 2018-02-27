package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboard.class)
public interface ContestScoreboard {
    ContestScoreboardType getType();
    String getScoreboard();

    class Builder extends ImmutableContestScoreboard.Builder {}
}
