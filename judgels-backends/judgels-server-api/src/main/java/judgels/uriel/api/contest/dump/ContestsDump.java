package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestsDump.class)
public interface ContestsDump {
    Set<ContestDump> getContests();

    class Builder extends ImmutableContestsDump.Builder {}
}
