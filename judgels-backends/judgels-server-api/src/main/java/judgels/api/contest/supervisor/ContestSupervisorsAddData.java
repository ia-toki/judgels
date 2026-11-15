package judgels.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorsAddData.class)
public interface ContestSupervisorsAddData {
    Set<String> getUsernames();
    Set<SupervisorManagementPermission> getManagementPermissions();

    class Builder extends ImmutableContestSupervisorsAddData.Builder {}
}
