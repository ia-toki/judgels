package judgels.jerahmeel.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionSummaryResponse.class)
public interface SubmissionSummaryResponse extends judgels.sandalphon.api.submission.bundle.SubmissionSummaryResponse {
    SubmissionConfig getConfig();

    class Builder extends ImmutableSubmissionSummaryResponse.Builder {}
}
