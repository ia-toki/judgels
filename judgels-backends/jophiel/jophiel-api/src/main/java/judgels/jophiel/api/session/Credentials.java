package judgels.jophiel.api.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCredentials.class)
public interface Credentials {
    String getUsernameOrEmail();
    String getPassword();

    static Credentials of(String usernameOrEmail, String password) {
        return ImmutableCredentials.builder()
                .usernameOrEmail(usernameOrEmail)
                .password(password)
                .build();
    }
}
