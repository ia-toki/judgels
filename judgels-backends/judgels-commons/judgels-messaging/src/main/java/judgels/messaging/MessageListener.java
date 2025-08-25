package judgels.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import judgels.messaging.api.Message;
import judgels.messaging.rabbitmq.RabbitMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final ObjectMapper objectMapper;
    private final RabbitMQ rabbitMQ;
    private Channel channel;

    @Inject
    public MessageListener(ObjectMapper objectMapper, RabbitMQ rabbitMQ) {
        this.objectMapper = objectMapper;
        this.rabbitMQ = rabbitMQ;
    }

    public void start(String queueName, ThreadPoolExecutor executorService, Consumer<Message> messageConsumer) throws IOException, TimeoutException {
        Connection connection = rabbitMQ.getConnection();

        channel = connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        channel.basicQos(executorService.getCorePoolSize());
        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                CompletableFuture.runAsync(() -> {
                    Message message;
                    try {
                        message = objectMapper.readValue(body, Message.class);
                    } catch (IOException e) {
                        LOGGER.error("Failed to deserialize RabbitMQ message", e);
                        return;
                    }
                    messageConsumer.accept(message);
                }, executorService).thenRun(() -> {
                    try {
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (IOException e) {
                        LOGGER.error("Failed to ack message", e);
                        throw new CompletionException(e);
                    }
                }).exceptionally(ex -> {
                    LOGGER.error("Failed to process message", ex);
                    try {
                        channel.basicNack(envelope.getDeliveryTag(), false, false);
                    } catch (IOException e) {
                        LOGGER.error("Failed to nack message", e);
                    }
                    return null;
                });
            }
        });
    }

    public void stop() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                LOGGER.error("Failed to close RabbitMQ channel", e);
            }
        }
    }
}
