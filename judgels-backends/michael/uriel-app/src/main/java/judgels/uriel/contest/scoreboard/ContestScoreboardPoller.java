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

    private static final Set<String> CONTEST_JIDS_IN_PROGRESS = Sets.newHashSet();

    private final ContestStore contestStore;
    private final ExecutorService executorService;
    private final ContestScoreboardUpdater contestScoreboardUpdater;

    public ContestScoreboardPoller(
            ContestStore contestStore,
            ExecutorService executorService,
            ContestScoreboardUpdater contestScoreboardUpdater) {

        this.contestStore = contestStore;
        this.executorService = executorService;
        this.contestScoreboardUpdater = contestScoreboardUpdater;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public void run() {
        try {
            for (Contest contest : contestStore.getRunningContests()) {
                updateContestAsync(contest);
            }
        } catch (Throwable e) {
            LOGGER.error("Failed to run contest scoreboard poller", e);
        }
    }

    public synchronized void updateContestAsync(Contest contest) {
        if (CONTEST_JIDS_IN_PROGRESS.contains(contest.getJid())) {
            return;
        }

        CONTEST_JIDS_IN_PROGRESS.add(contest.getJid());
        CompletableFuture.runAsync(() -> contestScoreboardUpdater.update(contest), executorService)
                .exceptionally(e -> {
                    LOGGER.error("Failed to process scoreboard of contest " + contest.getJid(), e);
                    return null;
                })
                .thenRun(() -> removeContestUpdater(contest));
    }

    private synchronized void removeContestUpdater(Contest contest) {
        CONTEST_JIDS_IN_PROGRESS.remove(contest.getJid());
    }
}
