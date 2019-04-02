package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.Dump;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagerDump.class)
public interface ContestManagerDump extends Dump {
    String getUserJid();

    class Builder extends ImmutableContestManagerDump.Builder {}
}
