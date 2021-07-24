package judgels.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import judgels.messaging.api.Message;
import judgels.messaging.rabbitmq.RabbitMQ;
import judgels.messaging.rabbitmq.RabbitMQChannel;
import judgels.messaging.rabbitmq.RabbitMQMessage;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClient.class);

    private final RabbitMQ rabbitMQ;
    private final ObjectMapper objectMapper;

    public MessageClient(RabbitMQ rabbitMQ, ObjectMapper objectMapper) {
        this.rabbitMQ = rabbitMQ;
        this.objectMapper = objectMapper;
    }

    public Optional<Message> receiveMessage(String queueName) {
        try {
            RabbitMQChannel channel = rabbitMQ.createChannel();
            channel.declareQueue(queueName);

            Optional<RabbitMQMessage> queueMessage = channel.popMessage(queueName);
            if (!queueMessage.isPresent()) {
                return Optional.empty();
            }

            ClientMessage clientMessage;
            try {
                clientMessage = objectMapper.readValue(queueMessage.get().getMessage(), ClientMessage.class);
            } catch (IOException e) {
                LOGGER.error("Could not deserialize client message", e);
                return Optional.empty();
            }

            Message message = new Message.Builder()
                    .id(queueMessage.get().getId())
                    .sourceQueueName(clientMessage.getSourceQueueName())
                    .type(clientMessage.getType())
                    .content(clientMessage.getContent())
                    .build();
            return Optional.of(message);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void confirmMessage(long messageId) {
        try {
            RabbitMQChannel channel = rabbitMQ.createChannel();
            channel.ackMessage(messageId);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void retryMessage(long messageId) {
        try {
            RabbitMQChannel channel = rabbitMQ.createChannel();
            channel.rejectMessage(messageId);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String sourceQueueName, String targetQueueName, String type, String content) {
        try {
            ClientMessage clientMessage = new ClientMessage.Builder()
                    .sourceQueueName(sourceQueueName)
                    .type(type)
                    .content(content)
                    .build();

            String message;
            try {
                message = objectMapper.writeValueAsString(clientMessage);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            RabbitMQChannel channel = rabbitMQ.createChannel();
            channel.declareQueue(targetQueueName);
            channel.pushMessage(targetQueueName, message);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableClientMessage.class)
    interface ClientMessage {
        String getSourceQueueName();
        String getType();
        String getContent();

        class Builder extends ImmutableClientMessage.Builder {}
    }
}
