package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableScoringConfig.class)
public interface ScoringConfig {
    ScoringConfig DEFAULT = new Builder().build();

    ScoringRoundingMode getRoundingMode();

    class Builder extends ImmutableScoringConfig.Builder {
        public Builder() {
            roundingMode(ScoringRoundingMode.ROUND);
        }
    }
}
