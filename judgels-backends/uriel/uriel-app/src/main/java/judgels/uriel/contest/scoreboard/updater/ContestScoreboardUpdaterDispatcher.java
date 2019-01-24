package judgels.uriel.contest.scoreboard.updater;

import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestStore;

@Singleton
public class ContestScoreboardUpdaterDispatcher implements Runnable {
    static final int THREAD_NUMBER = 5;
    private static final Set<String> UPDATER_JIDS = Sets.newHashSet();

    private final ContestStore contestStore;
    private final ExecutorService executorService;
    private final ContestScoreboardUpdater contestScoreboardUpdater;

    public ContestScoreboardUpdaterDispatcher(
            ContestStore contestStore,
            ExecutorService executorService,
            ContestScoreboardUpdater contestScoreboardUpdater) {
        this.contestStore = contestStore;
        this.executorService = executorService;
        this.contestScoreboardUpdater = contestScoreboardUpdater;
    }

    @Override
    @UnitOfWork
    public void run() {
        List<Contest> contests = contestStore.getRunningContests();
        for (Contest contest : contests) {
            updateAsync(contest);
        }
    }

    public synchronized void updateAsync(Contest contest) {
        if (!UPDATER_JIDS.contains(contest.getJid())) {
            UPDATER_JIDS.add(contest.getJid());
            try {
                CompletableFuture.runAsync(contestScoreboardUpdater.initJob(contest), executorService)
                        .thenRun(() -> removeUpdater(contest));
            } catch (CloneNotSupportedException c) {
                throw new RuntimeException(c.getMessage());
            }
        }
    }

    private synchronized void removeUpdater(Contest contest) {
        UPDATER_JIDS.remove(contest.getJid());
    }
}
