package judgels.uriel.role;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.user.role.UserRoleStore;
import judgels.uriel.api.role.UrielRole;

@Singleton
public class RoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public RoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getUriel().orElse("").equals(UrielRole.ADMIN.name());
    }
}
