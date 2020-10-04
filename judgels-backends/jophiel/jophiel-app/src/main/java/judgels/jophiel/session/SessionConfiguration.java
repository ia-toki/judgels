package judgels.jophiel.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSessionConfiguration.class)
public interface SessionConfiguration {
    judgels.jophiel.session.SessionConfiguration DEFAULT = new SessionConfiguration.Builder()
            .maxConcurrentSessionsPerUser(-1)
            .build();

    // If maxConcurrentSessionsPerUser is not set or negative, concurrent session limitation is disabled.
    Optional<Integer> getMaxConcurrentSessionsPerUser();

    class Builder extends ImmutableSessionConfiguration.Builder {}
}
