package judgels.jerahmeel.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionConfig.class)
public interface SubmissionConfig {
    boolean getCanManage();
    List<String> getUserJids();
    List<String> getProblemJids();

    class Builder extends ImmutableSubmissionConfig.Builder {}
}
