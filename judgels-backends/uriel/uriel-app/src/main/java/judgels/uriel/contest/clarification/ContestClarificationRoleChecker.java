package judgels.uriel.contest.clarification;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.CLARIFICATION;

import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestClarificationRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestModuleStore moduleStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestClarificationRoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestModuleStore moduleStore,
            ContestSupervisorStore supervisorStore) {

        this.adminRoleDao = adminRoleDao;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
        this.moduleStore = moduleStore;
        this.supervisorStore = supervisorStore;
    }

    public boolean canCreateClarification(String userJid, Contest contest) {
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

    public boolean canViewOwnClarifications(String userJid, Contest contest) {
        if (!moduleStore.hasClarificationModule(contest.getJid())) {
            return false;
        }
        if (isSupervisorWithClarificationPermissionOrAbove(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && !moduleStore.hasPausedModule(contest.getJid())
                && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canViewAllClarifications(String userJid, Contest contest) {
        return moduleStore.hasClarificationModule(contest.getJid())
                && isSupervisorWithClarificationPermissionOrAbove(userJid, contest);
    }

    private boolean isSupervisorWithClarificationPermissionOrAbove(String userJid, Contest contest) {
        if (adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid())) {
            return true;
        }
        Optional<ContestSupervisor> supervisor = supervisorStore.getSupervisor(contest.getJid(), userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(CLARIFICATION);
    }
}
