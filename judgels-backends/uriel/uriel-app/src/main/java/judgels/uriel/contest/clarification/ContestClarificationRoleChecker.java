package judgels.uriel.contest.clarification;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.CLARIFICATION;

import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestClarificationRoleChecker {
    private final ContestRoleChecker contestRoleChecker;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestModuleStore moduleStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestClarificationRoleChecker(
            ContestRoleChecker contestRoleChecker,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestModuleStore moduleStore,
            ContestSupervisorStore supervisorStore) {

        this.contestRoleChecker = contestRoleChecker;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
        this.moduleStore = moduleStore;
        this.supervisorStore = supervisorStore;
    }

    public boolean canCreate(String userJid, Contest contest) {
        boolean can = contestRoleDao.isContestant(userJid, contest.getJid())
                && moduleStore.hasClarificationModule(contest.getJid())
                && !moduleStore.hasPausedModule(contest.getJid())
                && contestTimer.hasStarted(contest, userJid)
                && !contestTimer.hasFinished(contest, userJid);

        Optional<ClarificationTimeLimitModuleConfig> config =
                moduleStore.getClarificationTimeLimitModuleConfig(contest.getJid());
        if (config.isPresent()) {
            Duration currentDuration = contestTimer.getDurationFromBeginTime(contest);
            Duration allowedDuration = config.get().getClarificationDuration();
            can = can && currentDuration.compareTo(allowedDuration) < 0;
        }
        return can;
    }

    public boolean canViewOwn(String userJid, Contest contest) {
        if (canSupervise(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && moduleStore.hasClarificationModule(contest.getJid())
                && !moduleStore.hasPausedModule(contest.getJid())
                && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canSupervise(String userJid, Contest contest) {
        if (!moduleStore.hasClarificationModule(contest.getJid())) {
            return false;
        }
        return contestRoleChecker.canSupervise(userJid, contest);
    }

    public boolean canManage(String userJid, Contest contest) {
        if (!moduleStore.hasClarificationModule(contest.getJid())) {
            return false;
        }
        return contestRoleChecker.canManage(userJid, contest)
                || supervisorStore.isSupervisorWithManagementPermission(contest.getJid(), userJid, CLARIFICATION);
    }
}
