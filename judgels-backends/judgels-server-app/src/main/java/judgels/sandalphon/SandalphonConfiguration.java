package judgels.sandalphon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.GabrielClientConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandalphonConfiguration.class)
public interface SandalphonConfiguration {
    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    class Builder extends ImmutableSandalphonConfiguration.Builder {}
}
