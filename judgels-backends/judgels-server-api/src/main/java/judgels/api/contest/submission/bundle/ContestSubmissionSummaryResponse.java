package judgels.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.contest.submission.ContestSubmissionConfig;
import judgels.api.submission.bundle.SubmissionSummaryResponse;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionSummaryResponse.class)
public interface ContestSubmissionSummaryResponse extends SubmissionSummaryResponse {
    ContestSubmissionConfig getConfig();

    class Builder extends ImmutableContestSubmissionSummaryResponse.Builder {}
}
