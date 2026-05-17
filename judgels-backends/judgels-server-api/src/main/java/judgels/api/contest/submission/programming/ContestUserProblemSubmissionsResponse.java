package judgels.api.contest.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import judgels.api.submission.programming.Submission;
import judgels.grading.api.SubmissionSource;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestUserProblemSubmissionsResponse.class)
public interface ContestUserProblemSubmissionsResponse {
    List<Submission> getData();
    Optional<SubmissionSource> getLatestSubmissionSource();

    class Builder extends ImmutableContestUserProblemSubmissionsResponse.Builder {}
}
