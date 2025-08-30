package judgels.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import judgels.messaging.api.Message;
import judgels.messaging.rabbitmq.RabbitMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClient.class);

    private final ObjectMapper objectMapper;
    private final RabbitMQ rabbitMQ;

    public MessageClient(ObjectMapper objectMapper, RabbitMQ rabbitMQ) {
        this.objectMapper = objectMapper;
        this.rabbitMQ = rabbitMQ;
    }

    public void sendMessage(String sourceQueueName, String targetQueueName, String type, String content) {
        Message message = new Message.Builder()
                .sourceQueueName(sourceQueueName)
                .type(type)
                .content(content)
                .build();

        String queueMessage;
        try {
            queueMessage = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Channel channel = null;
        try {
            channel = rabbitMQ.getConnection().createChannel();
            channel.queueDeclare(targetQueueName, true, false, false, null);
            channel.basicPublish("", targetQueueName, MessageProperties.PERSISTENT_BASIC, queueMessage.getBytes());
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Failed to send message to RabbitMQ", e);
            throw new RuntimeException(e);
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    LOGGER.error("Failed to close RabbitMQ channel after sending message", e);
                }
            }
        }
    }
}
