package judgels.core.messaging;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import judgels.core.messaging.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delivers messages within this process, in place of RabbitMQ when it is not configured.
 */
public class FakeRabbitMQ {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeRabbitMQ.class);

    private final Map<String, Consumer<Message>> listeners = new ConcurrentHashMap<>();

    public void send(String queueName, Message message) {
        Consumer<Message> listener = listeners.get(queueName);
        if (listener == null) {
            LOGGER.info("RabbitMQ is not configured and nothing listens to {}; skipping sending message", queueName);
            return;
        }
        listener.accept(message);
    }

    public void listen(String queueName, Executor executor, Consumer<Message> messageConsumer) {
        listeners.put(queueName, message -> CompletableFuture
                .runAsync(() -> messageConsumer.accept(message), executor)
                .exceptionally(e -> {
                    LOGGER.error("Failed to process message", e);
                    return null;
                }));
    }
}
