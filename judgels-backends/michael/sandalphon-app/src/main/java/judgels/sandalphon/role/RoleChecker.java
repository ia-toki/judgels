package judgels.sandalphon.role;

import javax.inject.Inject;
import judgels.jophiel.api.actor.Actor;
import judgels.sandalphon.api.role.SandalphonRole;

public class RoleChecker {
    @Inject
    public RoleChecker() {}

    public boolean isAdmin(Actor actor) {
        return actor.getRole().getSandalphon().orElse("").equals(SandalphonRole.ADMIN.name());
    }

    public boolean isWriter(Actor actor) {
        return true; // TODO(fushar): create separate role if necessary
    }
}
