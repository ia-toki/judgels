package judgels.uriel.api.contest.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.submission.Submission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmission.class)
public interface ContestSubmission {
    Submission getSubmission();
    SubmissionSource getSource();

    class Builder extends ImmutableContestSubmission.Builder {}
}
