package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGabrielClientConfiguration.class)
public interface GabrielClientConfiguration {
    GabrielClientConfiguration DEFAULT = new Builder()
            .gradingRequestQueueName("grading-request")
            .gradingResponseQueueName("grading-response")
            .build();

    String getGradingRequestQueueName();
    String getGradingResponseQueueName();

    class Builder extends ImmutableGabrielClientConfiguration.Builder {}
}
