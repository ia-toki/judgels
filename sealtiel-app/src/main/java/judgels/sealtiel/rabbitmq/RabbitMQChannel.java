package judgels.sealtiel.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import judgels.sealtiel.queue.QueueChannel;
import judgels.sealtiel.queue.QueueMessage;

public class RabbitMQChannel implements QueueChannel {
    private final Channel channel;

    public RabbitMQChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void declareQueue(String queueName) throws IOException {
        channel.queueDeclare(queueName, true, false, false, Collections.emptyMap());
    }

    @Override
    public void pushMessage(String queueName, String message) throws IOException {
        channel.basicPublish("", queueName, MessageProperties.PERSISTENT_BASIC, message.getBytes());
    }

    @Override
    public Optional<QueueMessage> popMessage(String queueName) throws IOException {
        GetResponse maybeDelivery = channel.basicGet(queueName, false);
        return Optional.ofNullable(maybeDelivery).map(delivery ->
                QueueMessage.of(delivery.getEnvelope().getDeliveryTag(), new String(delivery.getBody())));
    }

    @Override
    public void ackMessage(long messageId) throws IOException {
        channel.basicAck(messageId, false);
    }
}
