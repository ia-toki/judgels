package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGraderConfiguration.class)
public interface GraderConfiguration {
    GraderConfiguration DEFAULT = new Builder()
            .gradingRequestQueueName("grading-request")
            .gradingResponseQueueName("grading-response")
            .build();

    String getGradingRequestQueueName();
    String getGradingResponseQueueName();

    class Builder extends ImmutableGraderConfiguration.Builder {}
}
