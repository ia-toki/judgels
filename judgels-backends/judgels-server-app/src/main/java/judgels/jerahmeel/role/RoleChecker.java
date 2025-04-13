package judgels.jerahmeel.role;

import jakarta.inject.Inject;
import judgels.jerahmeel.api.role.JerahmeelRole;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.user.role.UserRoleStore;

public class RoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public RoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getJerahmeel().orElse("").equals(JerahmeelRole.ADMIN.name());
    }
}
