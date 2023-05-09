package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.nio.file.Path;
import java.util.Optional;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsServerConfiguration.class)
public interface JudgelsServerConfiguration {
    Path getBaseDataDir();

    @JsonProperty("app")
    JudgelsAppConfiguration getAppConfig();

    @JsonProperty("rabbitmq")
    Optional<RabbitMQConfiguration> getRabbitMQConfig();

    class Builder extends ImmutableJudgelsServerConfiguration.Builder {}
}
