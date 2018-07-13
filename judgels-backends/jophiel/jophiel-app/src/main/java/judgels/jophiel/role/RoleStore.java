package judgels.jophiel.role;

import javax.inject.Inject;
import judgels.jophiel.api.role.Role;

public class RoleStore {
    private final SuperadminRoleStore superadminRoleStore;
    private final AdminRoleStore adminRoleStore;

    @Inject
    public RoleStore(SuperadminRoleStore superadminRoleStore, AdminRoleStore adminRoleStore) {
        this.superadminRoleStore = superadminRoleStore;
        this.adminRoleStore = adminRoleStore;
    }

    public Role getUserRole(String userJid) {
        if (superadminRoleStore.isSuperadmin(userJid)) {
            return Role.SUPERADMIN;
        } else if (adminRoleStore.isAdmin(userJid)) {
            return Role.ADMIN;
        } else {
            return Role.USER;
        }
    }
}
