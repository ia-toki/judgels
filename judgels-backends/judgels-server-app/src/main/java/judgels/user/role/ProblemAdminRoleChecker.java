package judgels.user.role;

import jakarta.inject.Inject;
import judgels.api.user.role.UserRole;
import judgels.session.Actor;

public class ProblemAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public ProblemAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(Actor actor) {
        return actor.getRoles().contains(UserRole.PROBLEM_ADMIN);
    }

    public boolean isAdmin(String userJid) {
        return userRoleStore.getRoles(userJid).contains(UserRole.PROBLEM_ADMIN);
    }

    public boolean isWriter(Actor actor) {
        return true; // TODO(fushar): create separate role if necessary
    }
}
