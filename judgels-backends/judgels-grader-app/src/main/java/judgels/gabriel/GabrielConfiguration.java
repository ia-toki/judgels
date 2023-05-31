package judgels.gabriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.cache.CacheConfiguration;
import judgels.gabriel.grading.GradingConfiguration;
import judgels.gabriel.isolate.IsolateConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGabrielConfiguration.class)
public interface GabrielConfiguration {
    @JsonProperty("grading")
    GradingConfiguration getGradingConfig();

    @JsonProperty("cache")
    CacheConfiguration getCacheConfig();

    @JsonProperty("isolate")
    Optional<IsolateConfiguration> getIsolateConfig();

    class Builder extends ImmutableGabrielConfiguration.Builder {}
}
