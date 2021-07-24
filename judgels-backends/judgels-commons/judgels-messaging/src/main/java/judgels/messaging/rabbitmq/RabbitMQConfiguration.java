package judgels.messaging.rabbitmq;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRabbitMQConfiguration.class)
public interface RabbitMQConfiguration {
    RabbitMQConfiguration DEFAULT = new Builder()
            .host("localhost")
            .port(5672)
            .username("guest")
            .password("guest")
            .virtualHost("/")
            .build();

    String getHost();
    int getPort();
    String getUsername();
    String getPassword();
    String getVirtualHost();

    class Builder extends ImmutableRabbitMQConfiguration.Builder {}
}
