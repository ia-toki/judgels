package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRawContestScoreboard.class)
public interface RawContestScoreboard {
    ContestScoreboardType getType();
    String getScoreboard();
    Instant getUpdatedTime();

    class Builder extends ImmutableRawContestScoreboard.Builder {}
}
