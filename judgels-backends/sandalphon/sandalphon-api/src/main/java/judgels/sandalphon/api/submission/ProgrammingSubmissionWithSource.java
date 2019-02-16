package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.SubmissionSource;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProgrammingSubmissionWithSource.class)
public interface ProgrammingSubmissionWithSource {
    ProgrammingSubmission getProgrammingSubmission();
    SubmissionSource getSource();

    class Builder extends ImmutableProgrammingSubmissionWithSource.Builder {}
}
