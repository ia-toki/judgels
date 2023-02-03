package judgels.uriel.api.contest.log;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestLog.class)
public interface ContestLog {
    String getContestJid();
    String getUserJid();
    String getEvent();
    Optional<String> getObject();
    Optional<String> getProblemJid();
    Optional<String> getIpAddress();
    Instant getTime();

    class Builder extends ImmutableContestLog.Builder {}
}
