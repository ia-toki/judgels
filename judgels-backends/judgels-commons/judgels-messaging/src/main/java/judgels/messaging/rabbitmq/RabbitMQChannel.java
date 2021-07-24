package judgels.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class RabbitMQChannel {
    private final Channel channel;

    public RabbitMQChannel(Channel channel) {
        this.channel = channel;
    }

    public void declareQueue(String queueName) throws IOException {
        channel.queueDeclare(queueName, true, false, false, Collections.emptyMap());
    }

    public void pushMessage(String queueName, String message) throws IOException {
        channel.basicPublish("", queueName, MessageProperties.PERSISTENT_BASIC, message.getBytes());
    }

    public Optional<RabbitMQMessage> popMessage(String queueName) throws IOException {
        GetResponse maybeDelivery = channel.basicGet(queueName, false);
        return Optional.ofNullable(maybeDelivery).map(delivery ->
                RabbitMQMessage.of(delivery.getEnvelope().getDeliveryTag(), new String(delivery.getBody())));
    }

    public void ackMessage(long messageId) throws IOException {
        channel.basicAck(messageId, false);
    }

    public void rejectMessage(long messageId) throws IOException {
        channel.basicReject(messageId, true);
    }
}
