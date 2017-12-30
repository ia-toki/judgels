package judgels.jophiel.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jophiel.JophielConfiguration;
import judgels.jophiel.recaptcha.RecaptchaWebConfiguration;
import judgels.jophiel.user.registration.UserRegistrationWebConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableWebConfiguration.class)
public interface WebConfiguration {
    @JsonProperty("recaptcha")
    Optional<RecaptchaWebConfiguration> getRecaptchaConfig();

    @JsonProperty("userRegistration")
    UserRegistrationWebConfiguration getUserRegistrationConfig();

    static WebConfiguration fromServerConfig(JophielConfiguration config) {
        return new WebConfiguration.Builder()
                .recaptchaConfig(
                        config.getRecaptchaConfig().map(RecaptchaWebConfiguration::fromServerConfig))
                .userRegistrationConfig(
                        UserRegistrationWebConfiguration.fromServerConfig(config.getUserRegistrationConfig()))
                .build();
    }

    class Builder extends ImmutableWebConfiguration.Builder {}
}
