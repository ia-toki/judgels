package judgels.uriel.api.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.contestant.ContestContestant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantDump.class)
public interface ContestContestantDump extends ContestContestant, Dump {

    class Builder extends ImmutableContestContestantDump.Builder {}
}
