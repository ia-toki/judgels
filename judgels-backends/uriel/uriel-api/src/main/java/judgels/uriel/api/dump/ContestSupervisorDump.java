package judgels.uriel.api.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorDump.class)
public interface ContestSupervisorDump extends ContestSupervisor, Dump {

    class Builder extends ImmutableContestSupervisorDump.Builder {}
}
