package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItemSubmission.class)
public interface ItemSubmission {
    long getId();
    String getJid();
    String getContainerJid();
    String getProblemJid();
    String getItemJid();
    String getAnswer();
    String getUserJid();
    Instant getTime();

    class Builder extends ImmutableItemSubmission.Builder {}
}
