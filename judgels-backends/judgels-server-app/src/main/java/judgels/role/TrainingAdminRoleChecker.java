package judgels.role;

import jakarta.inject.Inject;
import judgels.api.actor.Actor;
import judgels.api.role.TrainingAdminRole;
import judgels.api.user.role.UserRole;
import judgels.user.role.UserRoleStore;

public class TrainingAdminRoleChecker {
    private final UserRoleStore userRoleStore;

    @Inject
    public TrainingAdminRoleChecker(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    public boolean isAdmin(Actor actor) {
        return actor.getRole().getTraining().orElse("").equals(TrainingAdminRole.ADMIN.name());
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleStore.getRole(userJid);
        return role.getTraining().orElse("").equals(TrainingAdminRole.ADMIN.name());
    }
}
