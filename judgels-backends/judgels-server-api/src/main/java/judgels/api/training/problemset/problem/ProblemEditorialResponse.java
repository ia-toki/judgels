package judgels.api.training.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.catalog.problem.ProblemEditorialInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemEditorialResponse.class)
public interface ProblemEditorialResponse {
    ProblemEditorialInfo getEditorial();

    class Builder extends ImmutableProblemEditorialResponse.Builder {}
}
