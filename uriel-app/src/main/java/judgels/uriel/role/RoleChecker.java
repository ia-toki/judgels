package judgels.uriel.role;

import javax.inject.Inject;

public class RoleChecker {
    private final RoleStore roleStore;

    @Inject
    public RoleChecker(RoleStore roleStore) {
        this.roleStore = roleStore;
    }

    public boolean canCreateContest(String actorJid) {
        return roleStore.isAdmin(actorJid);
    }

    public boolean canReadContest(String actorJid, String contestJid) {
        return roleStore.isAdmin(actorJid) || roleStore.isContestant(actorJid, contestJid);
    }

    public boolean canAddContestants(String actorJid) {
        return roleStore.isAdmin(actorJid);
    }

}
