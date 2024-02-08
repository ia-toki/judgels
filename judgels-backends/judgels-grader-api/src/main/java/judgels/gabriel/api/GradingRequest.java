package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingRequest.class)
public interface GradingRequest {
    String getGradingJid();
    String getProblemJid();
    String getGradingLanguage();
    SubmissionSource getSubmissionSource();
    Instant getGradingLastUpdateTime();
    GradingOptions getGradingOptions();

    class Builder extends ImmutableGradingRequest.Builder {}
}
