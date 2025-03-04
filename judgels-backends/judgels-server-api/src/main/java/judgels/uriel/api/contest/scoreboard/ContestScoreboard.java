package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import judgels.uriel.api.contest.ContestStyle;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboard.class)
public interface ContestScoreboard {
    ContestScoreboardType getType();
    ContestStyle getStyle();
    int getTotalEntries();
    Instant getUpdatedTime();
    Scoreboard getScoreboard();

    class Builder extends ImmutableContestScoreboard.Builder {}
}
