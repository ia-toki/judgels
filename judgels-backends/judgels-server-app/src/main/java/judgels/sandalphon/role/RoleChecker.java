package judgels.sandalphon.role;

import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.role.UserRoleStore;
import judgels.sandalphon.api.role.SandalphonRole;

public class RoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public RoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(Actor actor) {
        return actor.getRole().getSandalphon().orElse("").equals(SandalphonRole.ADMIN.name());
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getSandalphon().orElse("").equals(SandalphonRole.ADMIN.name());
    }

    public boolean isWriter(Actor actor) {
        return true; // TODO(fushar): create separate role if necessary
    }
}
