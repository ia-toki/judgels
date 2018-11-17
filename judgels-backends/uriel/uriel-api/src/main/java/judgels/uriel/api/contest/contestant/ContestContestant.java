package judgels.uriel.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestant.class)
public interface ContestContestant {
    String getUserJid();
    Optional<ContestContestantStatus> getStatus();
    Optional<Instant> getContestStartTime();

    class Builder extends ImmutableContestContestant.Builder {}
}
