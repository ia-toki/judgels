package judgels.jerahmeel.role;

import javax.inject.Inject;
import judgels.jerahmeel.api.role.JerahmeelRole;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.role.UserRoleStore;

public class RoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public RoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getJophiel() == JophielRole.SUPERADMIN
                || role.getUriel().orElse("").equals(JerahmeelRole.ADMIN.name());
    }
}
