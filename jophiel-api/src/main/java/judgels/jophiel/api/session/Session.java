package judgels.jophiel.api.session;

import org.immutables.value.Value;

@Value.Immutable
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
