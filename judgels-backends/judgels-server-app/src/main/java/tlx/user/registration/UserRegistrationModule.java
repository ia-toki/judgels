package tlx.user.registration;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.mailer.Mailer;
import judgels.user.UserStore;
import judgels.user.info.UserInfoStore;
import tlx.auth.google.GoogleAuth;
import tlx.recaptcha.RecaptchaVerifier;
import tlx.user.registration.web.UserRegistrationWebConfig;

@Module
public class UserRegistrationModule {
    private final Optional<UserRegistrationConfiguration> config;
    private final Optional<UserRegistrationWebConfig> webConfig;

    public UserRegistrationModule() {
        this.config = Optional.empty();
        this.webConfig = Optional.empty();
    }

    public UserRegistrationModule(
            Optional<UserRegistrationConfiguration> config,
            Optional<UserRegistrationWebConfig> webConfig) {

        this.config = config;
        this.webConfig = webConfig;
    }

    @Provides
    Optional<UserRegistrationConfiguration> userRegistrationConfig() {
        return config;
    }

    @Provides
    Optional<UserRegistrationWebConfig> userRegistrationWebConfig() {
        return webConfig;
    }

    @Provides
    @Singleton
    Optional<UserRegisterer> userRegisterer(
            UserStore userStore,
            UserInfoStore userInfoStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            Optional<Mailer> mailer,
            Optional<RecaptchaVerifier> recaptchaVerifier,
            Optional<GoogleAuth> googleAuth) {

        if (config.isEmpty() || !config.get().getEnabled()) {
            return Optional.empty();
        }

        Optional<RecaptchaVerifier> actualRecaptchaVerifier = recaptchaVerifier;
        if (!config.get().getUseRecaptcha()) {
            actualRecaptchaVerifier = Optional.empty();
        }

        return Optional.of(new UserRegisterer(
                userStore,
                userInfoStore,
                userRegistrationEmailStore,
                new UserRegistrationEmailMailer(mailer.get(), config.get().getActivationEmailTemplate()),
                actualRecaptchaVerifier,
                googleAuth));
    }
}
