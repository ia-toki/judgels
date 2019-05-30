package judgels.gabriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.grading.GradingConfiguration;
import judgels.gabriel.moe.MoeConfiguration;
import judgels.sealtiel.api.SealtielClientConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGabrielConfiguration.class)
public interface GabrielConfiguration {
    String getBaseDataDir();

    @JsonProperty("grading")
    GradingConfiguration getGradingWorkerConfig();

    @JsonProperty("sealtiel")
    SealtielClientConfiguration getSealtielConfig();

    @JsonProperty("moe")
    Optional<MoeConfiguration> getMoeConfig();

    class Builder extends ImmutableGabrielConfiguration.Builder {}
}
