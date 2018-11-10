package judgels.jophiel.user;

import javax.inject.Inject;
import judgels.jophiel.role.AdminRoleStore;
import judgels.jophiel.role.SuperadminRoleStore;

public class UserRoleChecker {
    private final SuperadminRoleStore superadminRoleStore;
    private final AdminRoleStore adminRoleStore;

    @Inject
    public UserRoleChecker(SuperadminRoleStore superadminRoleStore, AdminRoleStore adminRoleStore) {
        this.superadminRoleStore = superadminRoleStore;
        this.adminRoleStore = adminRoleStore;
    }

    public boolean canAdminister(String actorJid) {
        return superadminRoleStore.isSuperadmin(actorJid)
                || adminRoleStore.isAdmin(actorJid);
    }

    public boolean canManage(String actorJid, String userJid) {
        return superadminRoleStore.isSuperadmin(actorJid)
                || adminRoleStore.isAdmin(actorJid)
                || actorJid.equals(userJid);
    }
}
