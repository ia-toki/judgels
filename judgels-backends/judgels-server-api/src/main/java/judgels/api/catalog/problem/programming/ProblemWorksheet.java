package judgels.api.catalog.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.api.catalog.problem.ProblemStatement;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemWorksheet.class)
public interface ProblemWorksheet {
    ProblemStatement getStatement();
    ProblemLimits getLimits();
    ProblemSubmissionConfig getSubmissionConfig();
    Optional<String> getReasonNotAllowedToSubmit();

    class Builder extends ImmutableProblemWorksheet.Builder {}
}
