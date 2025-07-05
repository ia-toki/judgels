package judgels.contrib.jophiel.user.registration;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.contrib.jophiel.auth.google.GoogleAuth;
import judgels.contrib.jophiel.recaptcha.RecaptchaVerifier;
import judgels.contrib.jophiel.user.registration.web.UserRegistrationWebConfig;
import judgels.jophiel.mailer.Mailer;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.info.UserInfoStore;

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
    Optional<UserRegistrationWebConfig> webConfig() {
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
