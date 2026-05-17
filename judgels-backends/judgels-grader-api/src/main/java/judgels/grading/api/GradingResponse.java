package judgels.grading.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingResponse.class)
public interface GradingResponse {
    String getGradingJid();
    GradingResult getResult();

    class Builder extends ImmutableGradingResponse.Builder {}
}
