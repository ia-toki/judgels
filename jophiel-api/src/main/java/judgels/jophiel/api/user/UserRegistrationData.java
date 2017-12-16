package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationData.class)
public interface UserRegistrationData {
    String getUsername();
    Optional<String> getName();
    String getPassword();
    String getEmail();

    class Builder extends ImmutableUserRegistrationData.Builder {}
}
