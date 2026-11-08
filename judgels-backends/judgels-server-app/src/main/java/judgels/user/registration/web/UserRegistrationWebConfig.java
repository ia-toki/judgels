package judgels.user.registration.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.core.recaptcha.web.RecaptchaWebConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationWebConfig.class)
public interface UserRegistrationWebConfig {
    boolean getRecaptchaEnabled();

    @JsonProperty("recaptcha")
    Optional<RecaptchaWebConfig> getRecaptchaConfig();

    class Builder extends ImmutableUserRegistrationWebConfig.Builder {}
}
