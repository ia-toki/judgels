package judgels.uriel.contest.log;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import judgels.uriel.api.contest.log.ContestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContestLogPoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContestLogPoller.class);

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
                    return;
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
