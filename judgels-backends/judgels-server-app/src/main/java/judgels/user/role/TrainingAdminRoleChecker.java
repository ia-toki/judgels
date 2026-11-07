package judgels.user.role;

import jakarta.inject.Inject;
import judgels.api.user.role.UserRole;
import judgels.session.Actor;

public class TrainingAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public TrainingAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(Actor actor) {
        return actor.getRoles().contains(UserRole.TRAINING_ADMIN);
    }

    public boolean isAdmin(String userJid) {
        return userRoleStore.getRoles(userJid).contains(UserRole.TRAINING_ADMIN);
    }
}
