package judgels.uriel.api.contest.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.submission.programming.SubmissionsResponse;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionsResponse.class)
public interface ContestSubmissionsResponse extends SubmissionsResponse {
    ContestSubmissionConfig getConfig();

    class Builder extends ImmutableContestSubmissionsResponse.Builder {}
}
