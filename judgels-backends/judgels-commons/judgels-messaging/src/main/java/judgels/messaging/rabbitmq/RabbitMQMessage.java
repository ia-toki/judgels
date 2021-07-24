package judgels.messaging.rabbitmq;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RabbitMQMessage {
    public abstract long getId();
    public abstract String getMessage();

    public static RabbitMQMessage of(long id, String message) {
        return ImmutableRabbitMQMessage.builder()
                .id(id)
                .message(message)
                .build();
    }
}
