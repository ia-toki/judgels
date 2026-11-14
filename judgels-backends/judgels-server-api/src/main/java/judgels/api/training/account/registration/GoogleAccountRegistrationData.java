package judgels.api.training.account.registration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGoogleAccountRegistrationData.class)
public interface GoogleAccountRegistrationData {
    String getIdToken();
    String getUsername();

    class Builder extends ImmutableGoogleAccountRegistrationData.Builder {}
}
