package judgels.sandalphon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.sealtiel.api.SealtielClientConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandalphonConfiguration.class)
public interface SandalphonConfiguration {
    String getBaseDataDir();

    @JsonProperty("jophiel")
    JophielClientConfiguration getJophielConfig();

    @JsonProperty("sealtiel")
    SealtielClientConfiguration getSealtielConfig();

    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    String getRaphaelBaseUrl();

    class Builder extends ImmutableSandalphonConfiguration.Builder {}
}
