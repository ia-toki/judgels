package judgels.api.training.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.training.submission.SubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingItemSubmissionsResponse.class)
public interface TrainingItemSubmissionsResponse extends judgels.api.submission.bundle.ItemSubmissionsResponse {
    SubmissionConfig getConfig();

    class Builder extends ImmutableTrainingItemSubmissionsResponse.Builder {}
}
