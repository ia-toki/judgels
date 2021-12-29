package judgels.jophiel.api.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGoogleCredentials.class)
public interface GoogleCredentials {
    String getIdToken();

    static GoogleCredentials of(String idToken) {
        return ImmutableGoogleCredentials.builder()
                .idToken(idToken)
                .build();
    }
}
