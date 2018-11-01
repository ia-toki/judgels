package judgels.uriel.role;

import javax.inject.Inject;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;

public class AdminRoleStore {
    private final AdminRoleDao adminRoleDao;

    @Inject
    public AdminRoleStore(AdminRoleDao adminRoleDao) {
        this.adminRoleDao = adminRoleDao;
    }

    public void upsertAdmin(String userJid) {
        if (!adminRoleDao.isAdmin(userJid)) {
            AdminRoleModel model = new AdminRoleModel();
            model.userJid = userJid;
            adminRoleDao.insert(model);
            adminRoleDao.invalidateCache(userJid);
        }
    }
}
