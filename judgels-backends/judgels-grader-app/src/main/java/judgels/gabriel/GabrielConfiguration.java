package judgels.gabriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.grading.GradingConfiguration;
import judgels.gabriel.moe.MoeConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGabrielConfiguration.class)
public interface GabrielConfiguration {
    @JsonProperty("grading")
    GradingConfiguration getGradingConfig();

    @JsonProperty("moe")
    Optional<MoeConfiguration> getMoeConfig();

    class Builder extends ImmutableGabrielConfiguration.Builder {}
}
