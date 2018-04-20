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

    public void addAdmin(String userJid) {
        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
    }
}
