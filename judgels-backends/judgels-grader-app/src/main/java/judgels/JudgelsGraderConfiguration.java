package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsGraderConfiguration.class)
public interface JudgelsGraderConfiguration {
    String getBaseDataDir();

    @JsonProperty("rabbitmq")
    RabbitMQConfiguration getRabbitMQConfig();
}
