package tlx.api.problemset.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.problem.bundle.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemWorksheet.class)
public interface ProblemSetProblemWorksheet
        extends tlx.api.problemset.problem.ProblemSetProblemWorksheet {

    ProblemWorksheet getWorksheet();

    class Builder extends ImmutableProblemSetProblemWorksheet.Builder {}
}
