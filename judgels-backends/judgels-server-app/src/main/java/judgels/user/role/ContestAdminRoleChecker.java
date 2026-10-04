package judgels.user.role;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import judgels.api.user.role.ContestAdminRole;
import judgels.api.user.role.UserRole;

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
