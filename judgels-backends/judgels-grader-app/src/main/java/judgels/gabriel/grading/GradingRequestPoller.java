package judgels.gabriel.grading;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.inject.Provider;
import judgels.messaging.MessageClient;
import judgels.messaging.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingRequestPoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingRequestPoller.class);

    private static final Duration POLLING_DELAY = Duration.ofSeconds(2);

    private final ExecutorService executorService;
    private final String queueName;
    private final MessageClient messageClient;
    private final Provider<GradingWorker> workerFactory;

    public GradingRequestPoller(
            ExecutorService executorService,
            String queueName,
            MessageClient messageClient,
            Provider<GradingWorker> workerFactory) {

        this.executorService = executorService;
        this.queueName = queueName;
        this.messageClient = messageClient;
        this.workerFactory = workerFactory;
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
                GradingWorker worker = workerFactory.get();

                CompletableFuture.runAsync(() -> worker.process(message), executorService)
                        .exceptionally(e -> {
                            LOGGER.error("Failed to process message: " + message, e);
                            return null;
                        });

                try {
                    Thread.sleep((int) (Math.random() * 6000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Throwable e) {
                LOGGER.error("Failed to run grading request poller", e);
            }
        }
    }
}
