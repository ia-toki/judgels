package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleSubmission.class)
public interface BundleSubmission {
    long getId();
    String getJid();
    String getProblemJid();
    String getAuthorJid();
    Instant getTime();
    BundleGrading getLatestGrading();

    class Builder extends ImmutableBundleSubmission.Builder {}
}
