package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemWorksheet.class)
public interface ProblemWorksheet {
    ProblemStatement getStatement();
    ProblemSubmissionConfiguration getSubmissionConfig();

    class Builder extends ImmutableProblemWorksheet.Builder {}
}
