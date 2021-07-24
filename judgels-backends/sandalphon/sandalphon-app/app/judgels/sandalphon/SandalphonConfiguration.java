package judgels.sandalphon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.GraderConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import judgels.service.api.client.Client;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandalphonConfiguration.class)
public interface SandalphonConfiguration {
    String getBaseDataDir();

    Set<Client> getClients();

    @JsonProperty("jophiel")
    JophielClientConfiguration getJophielConfig();

    @JsonProperty("grader")
    GraderConfiguration getGraderConfig();

    @JsonProperty("rabbitmq")
    Optional<RabbitMQConfiguration> getRabbitMQConfig();

    class Builder extends ImmutableSandalphonConfiguration.Builder {}
}
