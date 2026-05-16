package judgels.api.contest.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.contest.submission.ContestSubmissionConfig;
import judgels.api.submission.programming.SubmissionsResponse;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionsResponse.class)
public interface ContestSubmissionsResponse extends SubmissionsResponse {
    ContestSubmissionConfig getConfig();

    class Builder extends ImmutableContestSubmissionsResponse.Builder {}
}
