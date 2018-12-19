package judgels.uriel.contest;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;

    @Inject
    public ContestRoleChecker(AdminRoleDao adminRoleDao, ContestRoleDao contestRoleDao, ContestTimer contestTimer) {
        this.adminRoleDao = adminRoleDao;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canAdminister(String userJid) {
        return adminRoleDao.isAdmin(userJid);
    }

    public boolean canView(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canSupervise(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isSupervisorOrAbove(userJid, contest.getJid());
    }

    public boolean canManage(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid());
    }

    public boolean canStartVirtual(String userJid, Contest contest) {
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && contestTimer.hasBegun(contest)
                && !contestTimer.hasEnded(contest)
                && !contestTimer.hasStarted(contest, userJid);
    }

    public ContestRole getRole(String userJid, Contest contest) {
        if (canAdminister(userJid)) {
            return ContestRole.ADMIN;
        } else if (canManage(userJid, contest)) {
            return ContestRole.MANAGER;
        } else if (canSupervise(userJid, contest)) {
            return ContestRole.SUPERVISOR;
        }

        return ContestRole.CONTESTANT;
    }
}
