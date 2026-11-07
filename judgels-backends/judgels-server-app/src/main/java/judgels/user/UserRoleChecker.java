package judgels.user;

import jakarta.inject.Inject;
import judgels.api.user.role.UserRole;
import judgels.user.role.UserRoleStore;

public class UserRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public UserRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean canAdminister(String actorJid) {
        return userRoleStore.getRoles(actorJid).contains(UserRole.SYSTEM_ADMIN);
    }

    public boolean canManage(String actorJid, String userJid) {
        return canAdminister(actorJid) || actorJid.equals(userJid);
    }
}
