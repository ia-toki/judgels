package judgels.uriel.contest.contestant;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.CONTESTANT;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestContestantRoleChecker {
    private final ContestRoleChecker contestRoleChecker;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestModuleStore moduleStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestContestantRoleChecker(
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

    public boolean canViewApproved(String userJid, Contest contest) {
        return contestRoleChecker.canView(userJid, contest);
    }

    public boolean canRegister(String userJid, Contest contest) {
        return !contestRoleDao.isContestant(userJid, contest.getJid())
                && !canSupervise(userJid, contest)
                && moduleStore.hasRegistrationModule(contest.getJid())
                && !contestTimer.hasEnded(contest);
    }

    public boolean canUnregister(String userJid, Contest contest) {
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && moduleStore.hasRegistrationModule(contest.getJid())
                && !contestTimer.hasBegun(contest);
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

    public boolean canSupervise(String userJid, Contest contest) {
        return contestRoleChecker.canSupervise(userJid, contest);
    }

    public boolean canManage(String userJid, Contest contest) {
        return contestRoleChecker.canManage(userJid, contest)
                || supervisorStore.isSupervisorWithManagementPermission(contest.getJid(), userJid, CONTESTANT);
    }
}
