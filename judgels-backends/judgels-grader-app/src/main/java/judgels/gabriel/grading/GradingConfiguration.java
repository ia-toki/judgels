package judgels.gabriel.grading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingConfiguration.class)
public interface GradingConfiguration {
    String getGradingRequestQueueName();
    String getCacheBaseDataDir();
    int getNumWorkerThreads();

    class Builder extends ImmutableGradingConfiguration.Builder {}
}
