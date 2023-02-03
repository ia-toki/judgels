package judgels.jophiel.user.account;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.auth.google.GoogleAuth;
import judgels.jophiel.mailer.Mailer;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.jophiel.user.registration.web.UserRegistrationWebConfig;
import judgels.recaptcha.RecaptchaVerifier;

@Module
public class UserRegistrationModule {
    private final UserRegistrationConfiguration config;
    private final UserRegistrationWebConfig webConfig;

    public UserRegistrationModule(UserRegistrationConfiguration config, UserRegistrationWebConfig webConfig) {
        this.config = config;
        this.webConfig = webConfig;
    }

    @Provides
    UserRegistrationWebConfig webConfig() {
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

        if (!config.getEnabled()) {
            return Optional.empty();
        }

        Optional<RecaptchaVerifier> actualRecaptchaVerifier = recaptchaVerifier;
        if (!config.getUseRecaptcha()) {
            actualRecaptchaVerifier = Optional.empty();
        }

        return Optional.of(new UserRegisterer(
                userStore,
                userInfoStore,
                userRegistrationEmailStore,
                new UserRegistrationEmailMailer(mailer.get(), config.getActivationEmailTemplate()),
                actualRecaptchaVerifier,
                googleAuth));
    }
}
