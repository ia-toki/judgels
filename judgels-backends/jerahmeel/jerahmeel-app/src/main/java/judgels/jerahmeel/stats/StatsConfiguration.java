package judgels.jerahmeel.stats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStatsConfiguration.class)
public interface StatsConfiguration {
    StatsConfiguration DEFAULT = new Builder().build();

    @Value.Default
    default boolean getEnabled() {
        return false;
    }

    class Builder extends ImmutableStatsConfiguration.Builder {}
}
