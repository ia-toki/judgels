package judgels.sealtiel.rabbitmq;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRabbitMQConfiguration.class)
public interface RabbitMQConfiguration {
    String getHost();
    int getPort();
    String getUsername();
    String getPassword();
    String getVirtualHost();

    class Builder extends ImmutableRabbitMQConfiguration.Builder {}
}
