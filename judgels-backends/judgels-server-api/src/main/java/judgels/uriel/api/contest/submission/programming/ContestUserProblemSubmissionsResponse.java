package judgels.uriel.api.contest.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.submission.programming.Submission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestUserProblemSubmissionsResponse.class)
public interface ContestUserProblemSubmissionsResponse {
    List<Submission> getData();
    Optional<SubmissionSource> getLatestSubmissionSource();

    class Builder extends ImmutableContestUserProblemSubmissionsResponse.Builder {}
}
