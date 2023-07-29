package judgels.jophiel.user;

import javax.inject.Inject;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.role.UserRoleStore;

public class UserRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public UserRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean canAdminister(String actorJid) {
        UserRole role = userRoleStore.getRole(actorJid);
        String jophielRole = role.getJophiel().orElse("");
        return jophielRole.equals(JophielRole.SUPERADMIN.name())
                || jophielRole.equals(JophielRole.ADMIN.name());
    }

    public boolean canManage(String actorJid, String userJid) {
        UserRole role = userRoleStore.getRole(actorJid);
        String jophielRole = role.getJophiel().orElse("");
        return jophielRole.equals(JophielRole.SUPERADMIN.name())
                || jophielRole.equals(JophielRole.ADMIN.name())
                || actorJid.equals(userJid);
    }
}
