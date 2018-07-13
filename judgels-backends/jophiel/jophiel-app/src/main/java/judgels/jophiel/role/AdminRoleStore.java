package judgels.jophiel.role;

import javax.inject.Inject;
import judgels.jophiel.persistence.AdminRoleDao;
import judgels.jophiel.persistence.AdminRoleModel;

public class AdminRoleStore {
    private final AdminRoleDao adminRoleDao;

    @Inject
    public AdminRoleStore(AdminRoleDao adminRoleDao) {
        this.adminRoleDao = adminRoleDao;
    }

    public void addAdmin(String userJid) {
        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
    }

    public boolean isAdmin(String userJid) {
        return adminRoleDao.isAdmin(userJid);
    }
}
