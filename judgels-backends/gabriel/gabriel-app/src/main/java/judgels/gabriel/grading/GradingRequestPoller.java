package judgels.gabriel.grading;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.inject.Provider;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingRequestPoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingRequestPoller.class);

    private static final Duration POLLING_DELAY = Duration.ofSeconds(2);

    private final ExecutorService executorService;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final Provider<GradingWorker> workerFactory;

    public GradingRequestPoller(
            ExecutorService executorService,
            BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            Provider<GradingWorker> workerFactory) {

        this.executorService = executorService;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.workerFactory = workerFactory;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Optional<Message> maybeMessage = messageService.receiveMessage(sealtielClientAuthHeader);
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
            } catch (Throwable e) {
                LOGGER.error("Failed to run grading request poller", e);
            }
        }
    }
}
