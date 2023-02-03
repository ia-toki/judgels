package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Map;
import judgels.uriel.api.contest.ContestStyle;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableExternalScoreboardData.class)
public interface ExternalScoreboardData {
    String getReceiverSecret();
    String getContestJid();
    ContestStyle getContestStyle();
    Instant getUpdatedTime();
    Map<ContestScoreboardType, Scoreboard> getScoreboards();

    class Builder extends ImmutableExternalScoreboardData.Builder {}
}
