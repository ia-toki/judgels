package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorDump.class)
public interface ContestSupervisorDump extends Dump {
    String getUserJid();
    Set<SupervisorManagementPermission> getManagementPermissions();

    class Builder extends ImmutableContestSupervisorDump.Builder {}
}
