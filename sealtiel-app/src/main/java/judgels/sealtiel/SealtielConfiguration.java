package judgels.sealtiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.client.api.Client;
import judgels.sealtiel.rabbitmq.RabbitMQConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSealtielConfiguration.class)
public interface SealtielConfiguration {
    Set<Client> getClients();

    @JsonProperty("rabbitmq")
    RabbitMQConfiguration getRabbitMQConfig();
}
