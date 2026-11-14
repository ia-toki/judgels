package judgels.api.training.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.catalog.problem.editorial.ProblemEditorialInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemEditorialResponse.class)
public interface ProblemSetProblemEditorialResponse {
    ProblemEditorialInfo getEditorial();

    class Builder extends ImmutableProblemSetProblemEditorialResponse.Builder {}
}
