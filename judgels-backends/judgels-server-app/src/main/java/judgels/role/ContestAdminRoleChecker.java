package judgels.role;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import judgels.api.role.ContestAdminRole;
import judgels.api.user.role.UserRole;
import judgels.user.role.UserRoleStore;

@Singleton
public class ContestAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public ContestAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getContest().orElse("").equals(ContestAdminRole.ADMIN.name());
    }
}
