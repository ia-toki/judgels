package judgels.uriel.contest.scoreboard;

import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContestScoreboardPoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContestScoreboardPoller.class);

    private static final Set<String> UPDATER_JIDS = Sets.newHashSet();

    private final ContestStore contestStore;
    private final ExecutorService executorService;
    private final ContestScoreboardUpdater scoreboardUpdater;

    public ContestScoreboardPoller(
            ContestStore contestStore,
            ExecutorService executorService,
            ContestScoreboardUpdater scoreboardUpdater) {

        this.contestStore = contestStore;
        this.executorService = executorService;
        this.scoreboardUpdater = scoreboardUpdater;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public void run() {
        try {
            contestStore.getRunningContests().forEach(this::updateAsync);
        } catch (Throwable e) {
            LOGGER.error("Failed to run contest scoreboard poller", e);
        }
    }

    public synchronized void updateAsync(Contest contest) {
        if (UPDATER_JIDS.contains(contest.getJid())) {
            return;
        }

        UPDATER_JIDS.add(contest.getJid());
        CompletableFuture.runAsync(() -> scoreboardUpdater.update(contest), executorService)
                .exceptionally(e -> {
                    LOGGER.error("Failed to process scoreboard of contest " + contest.getJid(), e);
                    return null;
                })
                .thenRun(() -> removeUpdater(contest));
    }

    private synchronized void removeUpdater(Contest contest) {
        UPDATER_JIDS.remove(contest.getJid());
    }
}
