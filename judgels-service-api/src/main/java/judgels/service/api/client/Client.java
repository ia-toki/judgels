package judgels.service.api.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableClient.class)
public interface Client {
    String getJid();
    String getSecret();

    static Client of(String jid, String secret) {
        return ImmutableClient.builder()
                .jid(jid)
                .secret(secret)
                .build();
    }
}
