package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboard.class)
public interface ContestScoreboard {
    ContestScoreboardType getType();
    Scoreboard getScoreboard();
    int getTotalEntries();
    Instant getUpdatedTime();

    class Builder extends ImmutableContestScoreboard.Builder {}
}
