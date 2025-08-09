package judgels.contrib.jophiel.recaptcha.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.contrib.jophiel.recaptcha.RecaptchaConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRecaptchaWebConfig.class)
public interface RecaptchaWebConfig {
    String getSiteKey();

    static RecaptchaWebConfig fromServerConfig(RecaptchaConfiguration config) {
        return new RecaptchaWebConfig.Builder()
                .siteKey(config.getSiteKey())
                .build();
    }

    class Builder extends ImmutableRecaptchaWebConfig.Builder {}
}
