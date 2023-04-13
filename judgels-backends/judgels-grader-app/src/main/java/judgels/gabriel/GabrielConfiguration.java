package judgels.gabriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.grading.GradingConfiguration;
import judgels.gabriel.moe.MoeConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGabrielConfiguration.class)
public interface GabrielConfiguration {
    String getBaseDataDir();

    @JsonProperty("grading")
    GradingConfiguration getGradingWorkerConfig();

    @JsonProperty("rabbitmq")
    RabbitMQConfiguration getRabbitMQConfig();

    @JsonProperty("moe")
    Optional<MoeConfiguration> getMoeConfig();

    class Builder extends ImmutableGabrielConfiguration.Builder {}
}
