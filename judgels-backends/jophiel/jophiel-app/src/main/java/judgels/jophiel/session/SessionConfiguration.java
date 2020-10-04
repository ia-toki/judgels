package judgels.jophiel.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSessionConfiguration.class)
public interface SessionConfiguration {
    judgels.jophiel.session.SessionConfiguration DEFAULT = new SessionConfiguration.Builder()
            .maxConcurrentSessionsPerUser(-1)
            .disableLogOut(false)
            .build();

    // If maxConcurrentSessionsPerUser is not set or negative, concurrent session limitation is disabled.
    // This limitation does not apply for admins.
    Optional<Integer> getMaxConcurrentSessionsPerUser();

    // If true, disable self log out. This limitation does not apply log outs done by admins.
    Optional<Boolean> getDisableLogOut();

    class Builder extends ImmutableSessionConfiguration.Builder {}
}
