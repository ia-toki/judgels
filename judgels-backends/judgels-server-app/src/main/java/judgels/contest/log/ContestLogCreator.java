package judgels.contest.log;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Duration;
import java.util.Optional;
import judgels.api.contest.Contest;
import judgels.api.contest.log.ContestLog;
import judgels.api.contest.role.ContestRole;
import judgels.contest.ContestRoleChecker;
import judgels.contest.ContestStore;

public class ContestLogCreator {
    private final ContestStore contestStore;
    private final ContestRoleChecker roleChecker;
    private final ContestLogStore logStore;

    public ContestLogCreator(
            ContestStore contestStore,
            ContestRoleChecker roleChecker,
            ContestLogStore logStore) {

        this.contestStore = contestStore;
        this.roleChecker = roleChecker;
        this.logStore = logStore;
    }

    @UnitOfWork
    public void createLog(ContestLog log) {
        Optional<Contest> contest = contestStore.getContestByJid(log.getContestJid());
        if (!contest.isPresent()) {
            return;
        }
        if (roleChecker.getRole(log.getUserJid(), contest.get()) == ContestRole.NONE) {
            return;
        }
        if (log.getTime().isAfter(contest.get().getEndTime().plus(Duration.ofDays(1)))) {
            return;
        }
        logStore.createLog(log);
    }
}
