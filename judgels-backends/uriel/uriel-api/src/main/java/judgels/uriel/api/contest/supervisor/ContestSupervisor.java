package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisor.class)
public interface ContestSupervisor {
    String getUserJid();
    Set<SupervisorManagementPermission> getManagementPermissions();

    class Builder extends ImmutableContestSupervisor.Builder {}
}
