package judgels.jophiel.role;

import javax.inject.Inject;

public class RoleChecker {
    private final RoleStore roleStore;

    @Inject
    public RoleChecker(RoleStore roleStore) {
        this.roleStore = roleStore;
    }

    public boolean canCreateUser(String actorJid) {
        return roleStore.isAdmin(actorJid);
    }

    public boolean canReadUser(String actorJid, String userJid) {
        return actorJid.equals(userJid) || roleStore.isAdmin(actorJid);
    }

    public boolean canReadUsers(String actorJid) {
        return roleStore.isAdmin(actorJid);
    }

    public boolean canMutateUser(String actorJid, String userJid) {
        return actorJid.equals(userJid) || roleStore.isAdmin(actorJid);
    }
}
