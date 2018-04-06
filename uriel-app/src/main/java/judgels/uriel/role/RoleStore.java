package judgels.uriel.role;

import javax.inject.Inject;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestRoleDao;

public class RoleStore {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;

    @Inject
    public RoleStore(AdminRoleDao adminRoleDao, ContestRoleDao contestRoleDao) {
        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
    }

    public void addAdmin(String userJid) {
        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
    }

    public boolean isAdmin(String userJid) {
        return adminRoleDao.existsByUserJid(userJid);
    }

    public boolean isContestContestantOrAbove(String userJid, String contestJid) {
        return contestRoleDao.isContestantOrAbove(userJid, contestJid);
    }

    public boolean isContestSupervisorOrAbove(String userJid, String contestJid) {
        return contestRoleDao.isSupervisorOrAbove(userJid, contestJid);
    }

    public boolean isContestManager(String userJid, String contestJid) {
        return contestRoleDao.isManager(userJid, contestJid);
    }
}
