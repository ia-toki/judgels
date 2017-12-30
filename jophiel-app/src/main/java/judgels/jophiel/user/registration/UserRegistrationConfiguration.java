package judgels.jophiel.user.registration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationConfiguration.class)
public interface UserRegistrationConfiguration {
    UserRegistrationConfiguration DEFAULT = new UserRegistrationConfiguration.Builder()
            .enabled(true)
            .useRecaptcha(false)
            .build();

    boolean getEnabled();
    boolean getUseRecaptcha();

    class Builder extends ImmutableUserRegistrationConfiguration.Builder {}
}
