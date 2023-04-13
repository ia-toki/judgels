package judgels.jophiel.api.user.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGoogleUserRegistrationData.class)
public interface GoogleUserRegistrationData {
    String getIdToken();
    String getUsername();

    class Builder extends ImmutableGoogleUserRegistrationData.Builder {}
}
