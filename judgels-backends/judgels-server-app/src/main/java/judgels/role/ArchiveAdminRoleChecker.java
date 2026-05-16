package judgels.role;

import jakarta.inject.Inject;
import judgels.api.role.ArchiveAdminRole;
import judgels.api.user.role.UserRole;
import judgels.user.role.UserRoleStore;

public class ArchiveAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public ArchiveAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getJerahmeel().orElse("").equals(ArchiveAdminRole.ADMIN.name());
    }
}
