package judgels.contrib.user.registration.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.JudgelsServerConfiguration;
import judgels.contrib.recaptcha.web.RecaptchaWebConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationWebConfig.class)
public interface UserRegistrationWebConfig {
    boolean getUseRecaptcha();

    @JsonProperty("recaptcha")
    Optional<RecaptchaWebConfig> getRecaptchaConfig();

    static Optional<UserRegistrationWebConfig> fromServerConfig(JudgelsServerConfiguration config) {
        if (config.getUserRegistrationConfig().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new UserRegistrationWebConfig.Builder()
                .useRecaptcha(config.getUserRegistrationConfig().get().getUseRecaptcha())
                .recaptchaConfig(config.getRecaptchaConfig().map(RecaptchaWebConfig::fromServerConfig))
                .build());
    }

    class Builder extends ImmutableUserRegistrationWebConfig.Builder {}
}
