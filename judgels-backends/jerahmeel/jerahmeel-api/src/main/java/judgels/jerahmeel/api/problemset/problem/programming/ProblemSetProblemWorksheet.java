package judgels.jerahmeel.api.problemset.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.problem.programming.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemWorksheet.class)
public interface ProblemSetProblemWorksheet
        extends judgels.jerahmeel.api.problemset.problem.ProblemSetProblemWorksheet {

    ProblemWorksheet getWorksheet();

    class Builder extends ImmutableProblemSetProblemWorksheet.Builder {}
}
