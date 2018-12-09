package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorUpsertData.class)
public interface ContestSupervisorUpsertData {
    Set<String> getUsernames();
    Set<SupervisorManagementPermission> getManagementPermissions();

    class Builder extends ImmutableContestSupervisorUpsertData.Builder {}
}
