package judgels.uriel.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSupervisorManagementPermissions.class)
public interface SupervisorManagementPermissions {
    Set<SupervisorManagementPermission> getAllowedPermissions();
    boolean getIsAllowedAll();

    static SupervisorManagementPermissions all() {
        return new Builder().isAllowedAll(true).build();
    }

    static SupervisorManagementPermissions of(Set<SupervisorManagementPermission> types) {
        return new Builder().isAllowedAll(false).addAllAllowedPermissions(types).build();
    }

    class Builder extends ImmutableSupervisorManagementPermissions.Builder {}
}
