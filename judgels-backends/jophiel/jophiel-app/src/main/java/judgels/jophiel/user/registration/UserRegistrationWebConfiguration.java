package judgels.jophiel.user.registration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationWebConfiguration.class)
public interface UserRegistrationWebConfiguration {
    boolean getUseRecaptcha();

    static UserRegistrationWebConfiguration fromServerConfig(UserRegistrationConfiguration config) {
        return new UserRegistrationWebConfiguration.Builder()
                .useRecaptcha(config.getUseRecaptcha())
                .build();
    }

    class Builder extends ImmutableUserRegistrationWebConfiguration.Builder {}
}
