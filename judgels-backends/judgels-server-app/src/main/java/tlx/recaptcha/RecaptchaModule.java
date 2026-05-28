package tlx.recaptcha;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import tlx.TlxScope;

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
    @TlxScope
    Optional<RecaptchaVerifier> recaptchaVerifier() {
        return config.map(c -> new RecaptchaVerifier(c.getSecretKey()));
    }
}
