package judgels.uriel.role;

import javax.inject.Inject;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class RoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;

    @Inject
    public RoleChecker(AdminRoleDao adminRoleDao, ContestRoleDao contestRoleDao) {
        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canCreateContest(String userJid) {
        return adminRoleDao.isAdmin(userJid);
    }

    public boolean canViewContest(String userJid, String contestJid) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contestJid);
    }

    public boolean canViewScoreboard(String userJid, String contestJid) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contestJid);
    }

    public boolean canAddContestants(String userJid, String contestJid) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contestJid);
    }
}
