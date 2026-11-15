package judgels.user;

import com.google.common.collect.Sets;
import jakarta.inject.Inject;
import java.util.EnumSet;
import java.util.Set;
import judgels.api.user.role.UserRole;
import judgels.user.role.UserRoleStore;

public class UserRoleChecker {
    private static final Set<UserRole> ADMIN_ROLES = Sets.immutableEnumSet(
            UserRole.SYSTEM_ADMIN,
            UserRole.PROBLEM_ADMIN,
            UserRole.CONTEST_ADMIN,
            UserRole.TRAINING_ADMIN);
    private static final Set<UserRole> WRITER_ROLES = Sets.immutableEnumSet(UserRole.PROBLEM_WRITER);

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

    public Set<UserRole> getManageableRoles(String actorJid) {
        Set<UserRole> actorRoles = userRoleStore.getRoles(actorJid);
        Set<UserRole> manageableRoles = EnumSet.noneOf(UserRole.class);
        if (actorRoles.contains(UserRole.SYSTEM_ADMIN)) {
            manageableRoles.addAll(ADMIN_ROLES);
        }
        if (actorRoles.contains(UserRole.PROBLEM_ADMIN)) {
            manageableRoles.addAll(WRITER_ROLES);
        }
        return manageableRoles;
    }
}
