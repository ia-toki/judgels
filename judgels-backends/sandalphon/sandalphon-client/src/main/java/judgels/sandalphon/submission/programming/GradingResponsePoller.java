package judgels.sandalphon.submission.programming;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
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

    private static final Duration POLLING_DURATION = Duration.ofSeconds(2);

    private final Clock clock;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final ExecutorService executorService;
    private final GradingResponseProcessor processor;

    public GradingResponsePoller(
            Clock clock,
            BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            ExecutorService executorService,
            GradingResponseProcessor processor) {

        this.clock = clock;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.executorService = executorService;
        this.processor = processor;
    }

    @Override
    public void run() {
        Instant checkpoint = clock.instant();
        do {
            Optional<Message> maybeMessage = messageService.receiveMessage(sealtielClientAuthHeader);
            if (!maybeMessage.isPresent()) {
                break;
            }

            Message message = maybeMessage.get();
            CompletableFuture.runAsync(() -> processor.process(message), executorService)
                    .exceptionally(e -> {
                        LOGGER.error("Failed to process message: " + message, e);
                        return null;
                    });

        } while (Duration.between(checkpoint, clock.instant()).compareTo(POLLING_DURATION) < 0);
    }
}
