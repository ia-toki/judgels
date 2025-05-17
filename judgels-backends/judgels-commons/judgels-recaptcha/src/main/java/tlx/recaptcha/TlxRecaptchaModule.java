package tlx.recaptcha;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;

@Module
public class TlxRecaptchaModule {
    private final Optional<RecaptchaConfiguration> config;

    public TlxRecaptchaModule(Optional<RecaptchaConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    Optional<RecaptchaVerifier> recaptchaVerifier() {
        return config.map(c -> new RecaptchaVerifier(c.getSecretKey()));
    }
}
