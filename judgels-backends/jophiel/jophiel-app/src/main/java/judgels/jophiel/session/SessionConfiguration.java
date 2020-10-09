package judgels.jophiel.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSessionConfiguration.class)
public interface SessionConfiguration {
    SessionConfiguration DEFAULT = new SessionConfiguration.Builder()
            .maxConcurrentSessionsPerUser(-1)
            .disableLogout(false)
            .build();

    // If maxConcurrentSessionsPerUser negative, concurrent session limitation is disabled.
    // This limitation does not apply for admins.
    @Value.Default
    default int getMaxConcurrentSessionsPerUser() {
        return -1;
    }

    // If true, disable self log out. This limitation does not apply log outs done by admins.
    @Value.Default
    default boolean getDisableLogout() {
        return false;
    }

    class Builder extends ImmutableSessionConfiguration.Builder {}
}
