package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorData.class)
public interface ContestSupervisorData {
    String getUserJid();
    SupervisorPermission getPermission();

    class Builder extends ImmutableContestSupervisorData.Builder {}
}
