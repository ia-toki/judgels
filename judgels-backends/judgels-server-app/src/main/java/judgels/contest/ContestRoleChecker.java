package judgels.contest;

import jakarta.inject.Inject;
import judgels.api.contest.Contest;
import judgels.api.contest.role.ContestRole;
import judgels.persistence.ContestRoleDao;
import judgels.role.ContestAdminRoleChecker;

public class ContestRoleChecker {
    private final ContestAdminRoleChecker roleChecker;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;

    @Inject
    public ContestRoleChecker(ContestAdminRoleChecker roleChecker, ContestRoleDao contestRoleDao, ContestTimer contestTimer) {
        this.roleChecker = roleChecker;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canAdminister(String userJid) {
        return roleChecker.isAdmin(userJid);
    }

    public boolean canView(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canSupervise(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || contestRoleDao.isSupervisorOrAbove(userJid, contest.getJid());
    }

    public boolean canManage(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid());
    }

    public boolean canStartVirtual(String userJid, Contest contest) {
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && contestTimer.hasBegun(contest)
                && !contestTimer.hasEnded(contest)
                && !contestTimer.hasStarted(contest, userJid);
    }

    public boolean canResetVirtual(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid());
    }

    public ContestRole getRole(String userJid, Contest contest) {
        if (roleChecker.isAdmin(userJid)) {
            return ContestRole.ADMIN;
        } else if (contestRoleDao.isManager(userJid, contest.getJid())) {
            return ContestRole.MANAGER;
        } else if (contestRoleDao.isSupervisorOrAbove(userJid, contest.getJid())) {
            return ContestRole.SUPERVISOR;
        } else if (contestRoleDao.isContestant(userJid, contest.getJid())) {
            return ContestRole.CONTESTANT;
        }

        return ContestRole.NONE;
    }
}
