package judgels.jophiel.api.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCredentials.class)
public interface Credentials {
    String getUsername();
    String getPassword();

    static Credentials of(String username, String password) {
        return ImmutableCredentials.builder()
                .username(username)
                .password(password)
                .build();
    }
}
