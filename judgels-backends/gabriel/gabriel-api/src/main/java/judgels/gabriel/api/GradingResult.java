package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingResult.class)
public interface GradingResult {
    Verdict getVerdict();
    int getScore();
    String getDetails();

    class Builder extends ImmutableGradingResult.Builder {}
}
