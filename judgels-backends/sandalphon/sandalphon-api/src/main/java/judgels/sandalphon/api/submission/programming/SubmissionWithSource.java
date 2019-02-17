package judgels.sandalphon.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.SubmissionSource;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionWithSource.class)
public interface SubmissionWithSource {
    Submission getSubmission();
    SubmissionSource getSource();

    class Builder extends ImmutableSubmissionWithSource.Builder {}
}
