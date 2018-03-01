package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboard.class)
public interface ContestScoreboard {
    ContestScoreboardType getType();
    Scoreboard getScoreboard();
    Map<String, String> getContestantDisplayNames();

    class Builder extends ImmutableContestScoreboard.Builder {}
}
