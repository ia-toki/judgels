package judgels.recaptcha;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;

@Module
public class RecaptchaModule {
    private final Optional<RecaptchaConfiguration> config;

    public RecaptchaModule(Optional<RecaptchaConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    Optional<RecaptchaVerifier> recaptchaVerifier() {
        return config.map(c -> new RecaptchaVerifier(c.getSecretKey()));
    }
}
