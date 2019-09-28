package judgels.jerahmeel.api.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAdmin.class)
public interface Admin {
    String getUserJid();

    class Builder extends ImmutableAdmin.Builder {}
}
