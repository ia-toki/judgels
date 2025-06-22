package judgels.contrib.jophiel.user.registration.recaptcha;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;

@Module
public class RecaptchaModule {
    private final Optional<RecaptchaConfiguration> config;

    public RecaptchaModule() {
        this.config = Optional.empty();
    }

    public RecaptchaModule(Optional<RecaptchaConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    Optional<RecaptchaVerifier> recaptchaVerifier() {
        return config.map(c -> new RecaptchaVerifier(c.getSecretKey()));
    }
}
