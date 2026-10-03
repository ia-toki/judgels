package judgels.api.training.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingSubmissionConfig.class)
public interface TrainingSubmissionConfig {
    boolean getCanManage();
    List<String> getUserJids();
    List<String> getProblemJids();

    class Builder extends ImmutableTrainingSubmissionConfig.Builder {}
}
