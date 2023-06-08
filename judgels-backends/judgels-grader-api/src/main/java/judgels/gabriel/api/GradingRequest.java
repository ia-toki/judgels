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
    Instant getGradingLastUpdateTime();
    SubmissionSource getSubmissionSource();

    class Builder extends ImmutableGradingRequest.Builder {}
}
