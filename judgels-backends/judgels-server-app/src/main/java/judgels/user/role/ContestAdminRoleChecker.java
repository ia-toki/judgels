package judgels.user.role;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import judgels.api.user.role.UserRole;

@Singleton
public class ContestAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public ContestAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(String userJid) {
        return userRoleStore.getRoles(userJid).contains(UserRole.CONTEST_ADMIN);
    }
}
