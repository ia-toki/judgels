package judgels.uriel.api.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.manager.ContestManager;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagerDump.class)
public interface ContestManagerDump extends ContestManager, Dump {

    class Builder extends ImmutableContestManagerDump.Builder {}
}
