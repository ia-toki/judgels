package judgels.user.role;

import jakarta.inject.Inject;
import judgels.api.user.role.ProblemAdminRole;
import judgels.api.user.role.UserRole;
import judgels.session.Actor;

public class ProblemAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public ProblemAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(Actor actor) {
        return actor.getRole().getProblem().orElse("").equals(ProblemAdminRole.ADMIN.name());
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getProblem().orElse("").equals(ProblemAdminRole.ADMIN.name());
    }

    public boolean isWriter(Actor actor) {
        return true; // TODO(fushar): create separate role if necessary
    }
}
