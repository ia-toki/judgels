package judgels.sandalphon.submission.programming;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingResponsePoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponsePoller.class);

    private static final Duration POLLING_DELAY = Duration.ofSeconds(2);

    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final ExecutorService executorService;
    private final GradingResponseProcessor processor;

    public GradingResponsePoller(
            BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            ExecutorService executorService,
            GradingResponseProcessor processor) {

        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.executorService = executorService;
        this.processor = processor;
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
                CompletableFuture.runAsync(() -> processor.process(message), executorService)
                        .exceptionally(e -> {
                            LOGGER.error("Failed to process message: " + message, e);
                            messageService.retryMessage(sealtielClientAuthHeader, message.getId());
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
