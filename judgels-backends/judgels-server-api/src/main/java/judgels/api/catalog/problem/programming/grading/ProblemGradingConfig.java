package judgels.api.catalog.problem.programming.grading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemGradingConfig.class)
public interface ProblemGradingConfig {
    String getEngine();

    // The config of the engine above, in the shape that engine defines.
    JsonNode getConfig();

    class Builder extends ImmutableProblemGradingConfig.Builder {}
}
