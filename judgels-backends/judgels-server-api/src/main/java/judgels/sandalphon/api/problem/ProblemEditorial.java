package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemEditorial.class)
public interface ProblemEditorial {
    String getText();

    class Builder extends ImmutableProblemEditorial.Builder {}
}
