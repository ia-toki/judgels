package judgels.user;

import jakarta.inject.Inject;
import judgels.api.role.UserAdminRole;
import judgels.api.user.role.UserRole;
import judgels.user.role.UserRoleStore;

public class UserRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public UserRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean canAdminister(String actorJid) {
        UserRole role = userRoleStore.getRole(actorJid);
        String accountRole = role.getAccount().orElse("");
        return accountRole.equals(UserAdminRole.SUPERADMIN.name())
                || accountRole.equals(UserAdminRole.ADMIN.name());
    }

    public boolean canManage(String actorJid, String userJid) {
        UserRole role = userRoleStore.getRole(actorJid);
        String accountRole = role.getAccount().orElse("");
        return accountRole.equals(UserAdminRole.SUPERADMIN.name())
                || accountRole.equals(UserAdminRole.ADMIN.name())
                || actorJid.equals(userJid);
    }
}
