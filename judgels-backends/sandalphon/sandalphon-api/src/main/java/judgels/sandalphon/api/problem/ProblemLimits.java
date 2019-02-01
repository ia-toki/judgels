package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemLimits.class)
public interface ProblemLimits {
    int getTimeLimit();
    int getMemoryLimit();

    class Builder extends ImmutableProblemLimits.Builder {}
}
