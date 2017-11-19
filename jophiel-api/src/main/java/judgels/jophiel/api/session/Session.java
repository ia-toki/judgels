package judgels.jophiel.api.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSession.class)
public interface Session {
    String getToken();
    String getUserJid();

    static Session of(String token, String userJid) {
        return ImmutableSession.builder()
                .token(token)
                .userJid(userJid)
                .build();
    }
}
