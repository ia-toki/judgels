package judgels.uriel.contest.contestant;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.CONTESTANT;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestContestantRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestModuleStore moduleStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestContestantRoleChecker(
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

    public boolean canGetContestants(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canRegister(String userJid, Contest contest) {
        return !contestRoleDao.isContestant(userJid, contest.getJid())
                && moduleStore.hasRegistrationModule(contest.getJid())
                && !contestTimer.hasEnded(contest);
    }

    public boolean canUnregister(String userJid, Contest contest) {
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && moduleStore.hasRegistrationModule(contest.getJid())
                && !contestTimer.hasBegun(contest);
    }

    public boolean canSuperviseContestants(String userJid, Contest contest) {
        return isSupervisorWithContestantPermissionOrAbove(userJid, contest);
    }

    public ContestContestantState getContestantState(String userJid, Contest contest) {
        if (canRegister(userJid, contest)) {
            return ContestContestantState.REGISTRABLE;
        } else if (canUnregister(userJid, contest)) {
            return ContestContestantState.REGISTRANT;
        } else if (contestRoleDao.isContestant(userJid, contest.getJid())) {
            return ContestContestantState.CONTESTANT;
        }
        return ContestContestantState.NONE;
    }

    private boolean isSupervisorWithContestantPermissionOrAbove(String userJid, Contest contest) {
        if (adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid())) {
            return true;
        }
        Optional<ContestSupervisor> supervisor = supervisorStore.findSupervisor(contest.getJid(), userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(CONTESTANT);
    }
}
