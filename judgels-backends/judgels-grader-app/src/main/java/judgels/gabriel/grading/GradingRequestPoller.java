package judgels.gabriel.grading;

import jakarta.inject.Provider;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.messaging.MessageClient;
import judgels.messaging.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingRequestPoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingRequestPoller.class);

    private final ThreadPoolExecutor executorService;
    private final String queueName;
    private final MessageClient messageClient;
    private final Provider<GradingWorker> workerFactory;

    public GradingRequestPoller(
            ThreadPoolExecutor executorService,
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
                if (executorService.getQueue().remainingCapacity() == 0) {
                    sleep(2 * 1000);
                    continue;
                }

                Optional<Message> maybeMessage = messageClient.receiveMessage(queueName);
                if (maybeMessage.isEmpty()) {
                    sleep(2 * 1000);
                    continue;
                }

                Message message = maybeMessage.get();
                GradingWorker worker = workerFactory.get();

                CompletableFuture.runAsync(() -> worker.process(message), executorService)
                        .exceptionally(e -> {
                            LOGGER.error("Failed to process message: " + message, e);
                            return null;
                        });

                sleep((long) (Math.random() * 1000));
            } catch (Throwable e) {
                LOGGER.error("Failed to run grading request poller", e);
            }
        }
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
