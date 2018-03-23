package judgels.jophiel.role;

import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.persistence.AdminRoleModel;

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

    public Role getUserRole(String userJid) {
        if (userJid.equals(superadminUserJid)) {
            return Role.SUPERADMIN;
        } else if (adminRoleDao.existsByUserJid(userJid)) {
            return Role.ADMIN;
        } else {
            return Role.USER;
        }
    }

    public boolean isAdmin(String userJid) {
        return userJid.equals(superadminUserJid) || adminRoleDao.existsByUserJid(userJid);
    }
}
