package judgels.api.catalog.problem.programming.grading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemGradingEngineUpdateData.class)
public interface ProblemGradingEngineUpdateData {
    String getEngine();

    class Builder extends ImmutableProblemGradingEngineUpdateData.Builder {}
}
