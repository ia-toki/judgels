package judgels.role;

import jakarta.inject.Inject;
import judgels.api.actor.Actor;
import judgels.api.role.ProblemAdminRole;
import judgels.api.user.role.UserRole;
import judgels.user.role.UserRoleStore;

public class ProblemAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public ProblemAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(Actor actor) {
        return actor.getRole().getSandalphon().orElse("").equals(ProblemAdminRole.ADMIN.name());
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getSandalphon().orElse("").equals(ProblemAdminRole.ADMIN.name());
    }

    public boolean isWriter(Actor actor) {
        return true; // TODO(fushar): create separate role if necessary
    }
}
