package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.submission.bundle.SubmissionSummaryResponse;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionSummaryResponse.class)
public interface ContestSubmissionSummaryResponse extends SubmissionSummaryResponse {
    ContestSubmissionConfig getConfig();

    class Builder extends ImmutableContestSubmissionSummaryResponse.Builder {}
}
