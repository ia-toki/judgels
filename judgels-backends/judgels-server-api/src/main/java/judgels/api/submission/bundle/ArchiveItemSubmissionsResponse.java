package judgels.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.submission.SubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableArchiveItemSubmissionsResponse.class)
public interface ArchiveItemSubmissionsResponse extends judgels.api.submission.bundle.ItemSubmissionsResponse {
    SubmissionConfig getConfig();

    class Builder extends ImmutableArchiveItemSubmissionsResponse.Builder {}
}
