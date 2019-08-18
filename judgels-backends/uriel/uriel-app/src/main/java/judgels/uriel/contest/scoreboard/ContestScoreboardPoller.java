package judgels.uriel.contest.scoreboard;

import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.group.ContestGroup;
import judgels.uriel.contest.ContestGroupStore;
import judgels.uriel.contest.ContestStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContestScoreboardPoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContestScoreboardPoller.class);

    private static final Set<String> CONTEST_JIDS_IN_PROGRESS = Sets.newHashSet();
    private static final Set<String> CONTEST_GROUP_JIDS_IN_PROGRESS = Sets.newHashSet();

    private final ContestStore contestStore;
    private final ContestGroupStore contestGroupStore;
    private final ExecutorService executorService;
    private final ContestScoreboardUpdater contestScoreboardUpdater;
    private final ContestGroupScoreboardUpdater contestGroupScoreboardUpdater;

    public ContestScoreboardPoller(
            ContestStore contestStore,
            ContestGroupStore contestGroupStore,
            ExecutorService executorService,
            ContestScoreboardUpdater contestScoreboardUpdater,
            ContestGroupScoreboardUpdater contestGroupScoreboardUpdater) {

        this.contestStore = contestStore;
        this.contestGroupStore = contestGroupStore;
        this.executorService = executorService;
        this.contestScoreboardUpdater = contestScoreboardUpdater;
        this.contestGroupScoreboardUpdater = contestGroupScoreboardUpdater;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public void run() {
        try {
            Set<String> contestJids = new HashSet<>();
            for (Contest contest : contestStore.getRunningContests()) {
                contestJids.add(contest.getJid());
                updateContestAsync(contest);
            }

            for (ContestGroup contestGroup : contestGroupStore.getContestGroupsByContestJids(contestJids)) {
                updateContestGroupAsync(contestGroup);
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

    public synchronized void updateContestGroupAsync(ContestGroup contestGroup) {
        if (CONTEST_GROUP_JIDS_IN_PROGRESS.contains(contestGroup.getJid())) {
            return;
        }

        CONTEST_GROUP_JIDS_IN_PROGRESS.add(contestGroup.getJid());
        CompletableFuture.runAsync(() -> contestGroupScoreboardUpdater.update(contestGroup), executorService)
                .exceptionally(e -> {
                    LOGGER.error("Failed to process scoreboard of contest group " + contestGroup.getJid(), e);
                    return null;
                })
                .thenRun(() -> removeContestGroupUpdater(contestGroup));
    }

    private synchronized void removeContestUpdater(Contest contest) {
        CONTEST_JIDS_IN_PROGRESS.remove(contest.getJid());
    }

    private synchronized void removeContestGroupUpdater(ContestGroup contestGroup) {
        CONTEST_GROUP_JIDS_IN_PROGRESS.remove(contestGroup.getJid());
    }
}
