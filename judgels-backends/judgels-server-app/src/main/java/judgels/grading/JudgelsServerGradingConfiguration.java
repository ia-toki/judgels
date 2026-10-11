package judgels.grading;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsServerGradingConfiguration.class)
public interface JudgelsServerGradingConfiguration {
    String getGradingRequestQueueName();
    String getGradingResponseQueueName();

    @JsonProperty("fake")
    @Value.Default
    default boolean getFakeGraderEnabled() {
        return false;
    }

    class Builder extends ImmutableJudgelsServerGradingConfiguration.Builder {}
}
