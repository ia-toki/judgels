package judgels.grading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsGraderGradingConfiguration.class)
public interface JudgelsGraderGradingConfiguration {
    String getGradingRequestQueueName();
    int getNumWorkerThreads();

    class Builder extends ImmutableJudgelsGraderGradingConfiguration.Builder {}
}
