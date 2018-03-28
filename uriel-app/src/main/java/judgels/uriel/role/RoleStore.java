package judgels.uriel.role;

import javax.inject.Inject;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantDao;

public class RoleStore {
    private final AdminRoleDao adminRoleDao;
    private final ContestContestantDao contestantDao;

    @Inject
    public RoleStore(AdminRoleDao adminRoleDao, ContestContestantDao contestantDao) {
        this.adminRoleDao = adminRoleDao;
        this.contestantDao = contestantDao;
    }

    public void addAdmin(String userJid) {
        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
    }

    public boolean isAdmin(String userJid) {
        return adminRoleDao.existsByUserJid(userJid);
    }

    public boolean isContestant(String userJid, String contestJid) {
        return contestantDao.existsByContestJidAndUserJid(contestJid, userJid);
    }
}
