package judgels.jophiel.legacy.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLegacySession.class)
public interface LegacySession {
    String getToken();
    String getUserJid();
    String getAuthCode();

    class Builder extends ImmutableLegacySession.Builder {}
}
