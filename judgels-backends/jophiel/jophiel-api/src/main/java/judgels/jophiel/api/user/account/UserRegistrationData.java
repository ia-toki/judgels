package judgels.jophiel.api.user.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationData.class)
public interface UserRegistrationData {
    String getUsername();
    String getName();
    String getPassword();
    String getEmail();
    Optional<String> getRecaptchaResponse();

    class Builder extends ImmutableUserRegistrationData.Builder {}
}
