package judgels.jerahmeel.api.problemset.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.problem.bundle.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemWorksheet.class)
public interface ProblemSetProblemWorksheet
        extends judgels.jerahmeel.api.problemset.problem.ProblemSetProblemWorksheet {

    ProblemWorksheet getWorksheet();

    class Builder extends ImmutableProblemSetProblemWorksheet.Builder {}
}
