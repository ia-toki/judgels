package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingRequest.class)
public interface GradingRequest {
    String getGradingJid();
    String getProblemJid();
    String getGradingEngine();
    String getGradingLanguage();
    SubmissionSource getSubmissionSource();

    class Builder extends ImmutableGradingRequest.Builder {}
}
