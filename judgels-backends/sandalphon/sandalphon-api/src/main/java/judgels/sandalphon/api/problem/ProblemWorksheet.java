package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
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
