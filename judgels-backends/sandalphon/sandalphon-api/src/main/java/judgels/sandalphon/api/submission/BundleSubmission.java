package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleSubmission.class)
public interface BundleSubmission {
    long getId();
    String getJid();
    String getContainerJid();
    String getProblemJid();
    String getItemJid();
    String getValue();
    String getUserJid();
    Instant getTime();

    class Builder extends ImmutableBundleSubmission.Builder {}
}
