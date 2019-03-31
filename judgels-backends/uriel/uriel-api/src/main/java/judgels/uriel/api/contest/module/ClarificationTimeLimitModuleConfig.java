package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.immutables.value.Value;

@JsonTypeName("CLARIFICATION_TIME_LIMIT")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableClarificationTimeLimitModuleConfig.class)
public interface ClarificationTimeLimitModuleConfig extends ModuleConfig {
    ClarificationTimeLimitModuleConfig DEFAULT = new Builder()
            .clarificationDuration(Duration.ofHours(2))
            .build();

    Duration getClarificationDuration();

    class Builder extends ImmutableClarificationTimeLimitModuleConfig.Builder {}
}
