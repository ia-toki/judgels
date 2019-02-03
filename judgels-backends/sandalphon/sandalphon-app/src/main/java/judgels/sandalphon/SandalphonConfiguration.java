package judgels.sandalphon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.sealtiel.api.SealtielClientConfiguration;
import judgels.service.api.client.Client;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandalphonConfiguration.class)
public interface SandalphonConfiguration {
    String getBaseDataDir();

    Set<Client> getClients();

    @JsonProperty("jophiel")
    JophielClientConfiguration getJophielConfig();

    @JsonProperty("sealtiel")
    SealtielClientConfiguration getSealtielConfig();

    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    String getRaphaelBaseUrl();

    class Builder extends ImmutableSandalphonConfiguration.Builder {}
}
