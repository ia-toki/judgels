package judgels.jerahmeel.api.stats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserTopStatsEntry.class)
public interface UserTopStatsEntry {
    String getUserJid();
    int getTotalScores();

    class Builder extends ImmutableUserTopStatsEntry.Builder {}
}
