package judgels.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.contest.submission.ContestSubmissionConfig;
import judgels.api.submission.bundle.ItemSubmissionsResponse;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestItemSubmissionsResponse.class)
public interface ContestItemSubmissionsResponse extends ItemSubmissionsResponse {
    ContestSubmissionConfig getConfig();

    class Builder extends ImmutableContestItemSubmissionsResponse.Builder {}
}
