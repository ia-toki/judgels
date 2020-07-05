package judgels.uriel.contest.log;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.uriel.api.contest.log.ContestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContestLogPoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContestLogPoller.class);

    private static final Duration POLLING_DELAY = Duration.ofSeconds(2);

    private final Queue<ContestLog> logQueue;
    private final ExecutorService executorService;
    private final ContestLogCreator logCreator;

    @Inject
    public ContestLogPoller(
            @Named("ContestLogQueue") Queue<ContestLog> logQueue,
            ExecutorService executorService,
            ContestLogCreator logCreator) {

        this.logQueue = logQueue;
        this.executorService = executorService;
        this.logCreator = logCreator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ContestLog log = logQueue.poll();
                if (log == null) {
                    try {
                        Thread.sleep(POLLING_DELAY.toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                CompletableFuture.runAsync(() -> logCreator.createLog(log), executorService)
                        .exceptionally(e -> {
                            LOGGER.error("Failed to create log", e);
                            return null;
                        });
            } catch (Throwable e) {
                LOGGER.error("Failed to run contest log poller", e);
            }
        }
    }
}
