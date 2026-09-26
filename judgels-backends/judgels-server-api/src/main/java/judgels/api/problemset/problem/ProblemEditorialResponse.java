package judgels.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.problem.ProblemEditorialInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemEditorialResponse.class)
public interface ProblemEditorialResponse {
    ProblemEditorialInfo getEditorial();

    class Builder extends ImmutableProblemEditorialResponse.Builder {}
}
