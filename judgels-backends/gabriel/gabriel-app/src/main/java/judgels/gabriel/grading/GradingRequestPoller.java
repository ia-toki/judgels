package judgels.gabriel.grading;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
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

    private static final Duration POLLING_DURATION = Duration.ofSeconds(2);

    private final Clock clock;
    private final ExecutorService executorService;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final Provider<GradingWorker> workerFactory;

    public GradingRequestPoller(
            Clock clock,
            ExecutorService executorService,
            BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            Provider<GradingWorker> workerFactory) {

        this.clock = clock;
        this.executorService = executorService;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.workerFactory = workerFactory;
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
            GradingWorker worker = workerFactory.get();

            CompletableFuture.runAsync(() -> worker.process(message), executorService)
                    .exceptionally(e -> {
                        LOGGER.error("Failed to process message: " + message, e);
                        return null;
                    });

        } while (Duration.between(checkpoint, clock.instant()).compareTo(POLLING_DURATION) < 0);
    }
}
