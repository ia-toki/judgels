package judgels.uriel.contest.log;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Duration;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;

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
