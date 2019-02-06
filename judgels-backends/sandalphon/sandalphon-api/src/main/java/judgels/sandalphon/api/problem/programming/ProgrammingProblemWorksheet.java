package judgels.sandalphon.api.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProgrammingProblemWorksheet.class)
public interface ProgrammingProblemWorksheet {
    ProblemStatement getStatement();
    ProblemLimits getLimits();
    ProblemSubmissionConfig getSubmissionConfig();
    Optional<String> getReasonNotAllowedToSubmit();

    class Builder extends ImmutableProgrammingProblemWorksheet.Builder {}
}
