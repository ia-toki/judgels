package judgels.jophiel.recaptcha;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRecaptchaWebConfiguration.class)
public interface RecaptchaWebConfiguration {
    String getSiteKey();

    static RecaptchaWebConfiguration fromServerConfig(RecaptchaConfiguration config) {
        return new RecaptchaWebConfiguration.Builder()
                .siteKey(config.getSiteKey())
                .build();
    }

    class Builder extends ImmutableRecaptchaWebConfiguration.Builder {}
}
