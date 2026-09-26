package judgels.api.problemset.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.problem.programming.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemWorksheet.class)
public interface ProblemSetProblemWorksheet
        extends judgels.api.problemset.problem.ProblemSetProblemWorksheet {

    ProblemWorksheet getWorksheet();

    class Builder extends ImmutableProblemSetProblemWorksheet.Builder {}
}
