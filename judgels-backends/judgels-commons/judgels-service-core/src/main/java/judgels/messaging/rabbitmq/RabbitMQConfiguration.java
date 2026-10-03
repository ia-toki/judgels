package judgels.messaging.rabbitmq;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRabbitMQConfiguration.class)
public interface RabbitMQConfiguration {
    RabbitMQConfiguration DEFAULT = new Builder()
            .host("localhost")
            .username("guest")
            .password("guest")
            .build();

    String getHost();
    String getUsername();
    String getPassword();

    class Builder extends ImmutableRabbitMQConfiguration.Builder {}
}
