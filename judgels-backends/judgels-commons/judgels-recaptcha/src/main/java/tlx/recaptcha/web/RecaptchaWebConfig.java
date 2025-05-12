package tlx.recaptcha.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import tlx.recaptcha.RecaptchaConfiguration;

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
