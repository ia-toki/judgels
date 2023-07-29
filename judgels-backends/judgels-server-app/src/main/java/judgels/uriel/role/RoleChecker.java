package judgels.uriel.role;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.role.UserRoleStore;
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
