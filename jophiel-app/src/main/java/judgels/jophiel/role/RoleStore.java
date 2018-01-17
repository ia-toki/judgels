package judgels.jophiel.role;

import javax.inject.Inject;

public class RoleStore {
    private final AdminRoleDao adminRoleDao;
    private static String superadminUserJid;

    @Inject
    public RoleStore(AdminRoleDao adminRoleDao) {
        this.adminRoleDao = adminRoleDao;
    }

    public void setSuperadmin(String userJid) {
        superadminUserJid = userJid;
    }

    public void addAdmin(String userJid) {
        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
    }

    public boolean isAdmin(String userJid) {
        return userJid.equals(superadminUserJid) || adminRoleDao.existsByUserJid(userJid);
    }
}
