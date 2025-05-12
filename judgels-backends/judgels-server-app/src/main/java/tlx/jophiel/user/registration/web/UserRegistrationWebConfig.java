package tlx.jophiel.user.registration.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jophiel.JophielConfiguration;
import org.immutables.value.Value;
import tlx.recaptcha.web.RecaptchaWebConfig;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationWebConfig.class)
public interface UserRegistrationWebConfig {
    boolean getUseRecaptcha();

    @JsonProperty("recaptcha")
    Optional<RecaptchaWebConfig> getRecaptchaConfig();

    static UserRegistrationWebConfig fromServerConfig(JophielConfiguration config) {
        return new UserRegistrationWebConfig.Builder()
                .useRecaptcha(config.getUserRegistrationConfig().getUseRecaptcha())
                .recaptchaConfig(config.getRecaptchaConfig().map(RecaptchaWebConfig::fromServerConfig))
                .build();
    }

    class Builder extends ImmutableUserRegistrationWebConfig.Builder {}
}
