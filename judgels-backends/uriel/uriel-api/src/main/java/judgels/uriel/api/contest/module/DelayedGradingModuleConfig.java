package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableDelayedGradingModuleConfig.class)
public interface DelayedGradingModuleConfig {
    DelayedGradingModuleConfig DEFAULT = new Builder()
            .delayDuration(Duration.ofMinutes(5))
            .build();

    Duration getDelayDuration();

    class Builder extends ImmutableDelayedGradingModuleConfig.Builder {}
}
