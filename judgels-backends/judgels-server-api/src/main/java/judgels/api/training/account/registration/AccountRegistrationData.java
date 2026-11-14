package judgels.api.training.account.registration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAccountRegistrationData.class)
public interface AccountRegistrationData {
    String getUsername();
    String getName();
    String getPassword();
    String getEmail();
    Optional<String> getRecaptchaResponse();

    class Builder extends ImmutableAccountRegistrationData.Builder {}
}
