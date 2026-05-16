package judgels.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.submission.SubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableArchiveSubmissionSummaryResponse.class)
public interface ArchiveSubmissionSummaryResponse extends judgels.api.submission.bundle.SubmissionSummaryResponse {
    SubmissionConfig getConfig();

    class Builder extends ImmutableArchiveSubmissionSummaryResponse.Builder {}
}
