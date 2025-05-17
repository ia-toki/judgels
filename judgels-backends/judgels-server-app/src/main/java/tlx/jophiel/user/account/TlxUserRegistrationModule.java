package tlx.jophiel.user.account;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.jophiel.mailer.Mailer;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.account.UserRegistrationEmailStore;
import judgels.jophiel.user.info.UserInfoStore;
import tlx.jophiel.auth.google.GoogleAuth;
import tlx.jophiel.user.registration.web.UserRegistrationWebConfig;
import tlx.recaptcha.RecaptchaVerifier;

@Module
public class TlxUserRegistrationModule {
    private final UserRegistrationConfiguration config;
    private final UserRegistrationWebConfig webConfig;

    public TlxUserRegistrationModule(UserRegistrationConfiguration config, UserRegistrationWebConfig webConfig) {
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
