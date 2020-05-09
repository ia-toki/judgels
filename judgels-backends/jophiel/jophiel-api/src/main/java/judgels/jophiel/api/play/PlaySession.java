package judgels.jophiel.api.play;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePlaySession.class)
public interface PlaySession {
    String getAuthCode();
    String getToken();
    String getUserJid();
    String getUsername();
    String getRole();
    Optional<String> getName();

    class Builder extends ImmutablePlaySession.Builder {}
}
