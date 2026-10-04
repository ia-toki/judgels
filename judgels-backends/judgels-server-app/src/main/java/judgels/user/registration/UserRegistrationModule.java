package judgels.user.registration;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.core.auth.google.GoogleAuth;
import judgels.core.mailer.Mailer;
import judgels.core.recaptcha.RecaptchaVerifier;
import judgels.user.UserStore;
import judgels.user.info.UserInfoStore;
import judgels.user.registration.web.UserRegistrationWebConfig;

@Module
public class UserRegistrationModule {
    private final Optional<UserRegistrationWebConfig> webConfig;

    public UserRegistrationModule() {
        this.webConfig = Optional.empty();
    }

    public UserRegistrationModule(Optional<UserRegistrationWebConfig> webConfig) {
        this.webConfig = webConfig;
    }

    @Provides
    Optional<UserRegistrationWebConfig> userRegistrationWebConfig() {
        return webConfig;
    }

    @Provides
    @Singleton
    Optional<UserRegisterer> userRegisterer(
            Optional<UserRegistrationConfiguration> config,
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
