package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.contestant.ContestContestantStatus;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantDump.class)
public interface ContestContestantDump extends Dump {
    String getUserJid();
    Optional<ContestContestantStatus> getStatus();
    Optional<Instant> getContestStartTime();
    Optional<Integer> getFinalRank();

    class Builder extends ImmutableContestContestantDump.Builder {}
}
