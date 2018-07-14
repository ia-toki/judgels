package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisor.class)
public interface ContestSupervisor {
    String getUserJid();
    SupervisorPermission getPermission();

    class Builder extends ImmutableContestSupervisor.Builder {}
}
