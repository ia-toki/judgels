package judgels.jerahmeel.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItemSubmissionsResponse.class)
public interface ItemSubmissionsResponse extends judgels.sandalphon.api.submission.bundle.ItemSubmissionsResponse {
    class Builder extends ImmutableItemSubmissionsResponse.Builder {}
}
