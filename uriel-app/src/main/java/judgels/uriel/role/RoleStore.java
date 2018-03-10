package judgels.uriel.role;

import javax.inject.Inject;

public class RoleStore {
    private final AdminRoleDao adminRoleDao;

    @Inject
    public RoleStore(AdminRoleDao adminRoleDao) {
        this.adminRoleDao = adminRoleDao;
    }

    public void addAdmin(String userJid) {
        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
    }

    public boolean isAdmin(String userJid) {
        return adminRoleDao.existsByUserJid(userJid);
    }
}
