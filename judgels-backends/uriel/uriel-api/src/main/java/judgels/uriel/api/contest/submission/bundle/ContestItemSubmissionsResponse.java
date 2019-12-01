package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.submission.bundle.ItemSubmissionsResponse;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestItemSubmissionsResponse.class)
public interface ContestItemSubmissionsResponse extends ItemSubmissionsResponse {
    ContestSubmissionConfig getConfig();

    class Builder extends ImmutableContestItemSubmissionsResponse.Builder {}
}
