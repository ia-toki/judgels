package judgels.contrib.jophiel.user.registration.recaptcha;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRecaptchaConfiguration.class)
public interface RecaptchaConfiguration {
    String getSiteKey();
    String getSecretKey();

    class Builder extends ImmutableRecaptchaConfiguration.Builder {}
}
