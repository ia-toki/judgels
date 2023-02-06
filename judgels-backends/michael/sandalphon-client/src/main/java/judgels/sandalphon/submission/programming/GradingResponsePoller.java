package judgels.sandalphon.submission.programming;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import judgels.messaging.MessageClient;
import judgels.messaging.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingResponsePoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponsePoller.class);

    private static final Duration POLLING_DELAY = Duration.ofSeconds(2);

    private final String queueName;
    private final MessageClient messageClient;
    private final ExecutorService executorService;
    private final GradingResponseProcessor processor;

    public GradingResponsePoller(
            String queueName,
            MessageClient messageClient,
            ExecutorService executorService,
            GradingResponseProcessor processor) {

        this.queueName = queueName;
        this.messageClient = messageClient;
        this.executorService = executorService;
        this.processor = processor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Optional<Message> maybeMessage = messageClient.receiveMessage(queueName);
                if (!maybeMessage.isPresent()) {
                    try {
                        Thread.sleep(POLLING_DELAY.toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                Message message = maybeMessage.get();
                CompletableFuture.runAsync(() -> processor.process(message), executorService)
                        .exceptionally(e -> {
                            LOGGER.error("Failed to process message: " + message, e);
                            messageClient.retryMessage(message.getId());
                            return null;
                        });
            } catch (Throwable e) {
                LOGGER.error("Failed to run grading response poller", e);
                try {
                    Thread.sleep(POLLING_DELAY.toMillis());
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
