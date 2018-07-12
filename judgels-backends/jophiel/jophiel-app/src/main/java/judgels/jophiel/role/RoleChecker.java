package judgels.jophiel.role;

import javax.inject.Inject;

public class RoleChecker {
    private final SuperadminRoleStore superadminRoleStore;
    private final AdminRoleStore adminRoleStore;

    @Inject
    public RoleChecker(SuperadminRoleStore superadminRoleStore, AdminRoleStore adminRoleStore) {
        this.superadminRoleStore = superadminRoleStore;
        this.adminRoleStore = adminRoleStore;
    }

    public boolean canCreateUser(String actorJid) {
        return superadminRoleStore.isSuperadmin(actorJid)
                || adminRoleStore.isAdmin(actorJid);
    }

    public boolean canViewUser(String actorJid, String userJid) {
        return superadminRoleStore.isSuperadmin(actorJid)
                || adminRoleStore.isAdmin(actorJid)
                || actorJid.equals(userJid);
    }

    public boolean canViewUserList(String actorJid) {
        return superadminRoleStore.isSuperadmin(actorJid)
                || adminRoleStore.isAdmin(actorJid);
    }

    public boolean canUpdateUser(String actorJid, String userJid) {
        return superadminRoleStore.isSuperadmin(actorJid)
                || adminRoleStore.isAdmin(actorJid)
                || actorJid.equals(userJid);
    }

    public boolean canUpdateUserList(String actorJid) {
        return superadminRoleStore.isSuperadmin(actorJid)
                || adminRoleStore.isAdmin(actorJid);
    }
}
