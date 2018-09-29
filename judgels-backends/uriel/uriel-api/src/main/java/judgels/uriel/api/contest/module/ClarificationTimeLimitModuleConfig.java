package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableClarificationTimeLimitModuleConfig.class)
public interface ClarificationTimeLimitModuleConfig {
    ClarificationTimeLimitModuleConfig DEFAULT = new Builder()
            .clarificationDuration(Duration.ofHours(2))
            .build();

    Duration getClarificationDuration();

    class Builder extends ImmutableClarificationTimeLimitModuleConfig.Builder {}
}
