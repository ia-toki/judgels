package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSupervisorPermission.class)
public abstract class SupervisorPermission {
    public abstract Set<SupervisorPermissionType> getAllowedPermissions();
    public abstract boolean getIsAllowedAll();

    public static SupervisorPermission all() {
        return new Builder().isAllowedAll(true).build();
    }

    public static SupervisorPermission of(Set<SupervisorPermissionType> types) {
        return new Builder().isAllowedAll(false).addAllAllowedPermissions(types).build();
    }

    public final boolean allows(SupervisorPermissionType type) {
        return getIsAllowedAll() || getAllowedPermissions().contains(type);
    }

    public static class Builder extends ImmutableSupervisorPermission.Builder {}
}
