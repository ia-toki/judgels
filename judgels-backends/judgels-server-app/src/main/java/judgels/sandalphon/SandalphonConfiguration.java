package judgels.sandalphon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandalphonConfiguration.class)
public interface SandalphonConfiguration {
    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    @JsonProperty("rabbitmq")
    Optional<RabbitMQConfiguration> getRabbitMQConfig();

    class Builder extends ImmutableSandalphonConfiguration.Builder {}
}
