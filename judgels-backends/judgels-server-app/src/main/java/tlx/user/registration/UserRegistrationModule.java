package tlx.user.registration;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import judgels.mailer.Mailer;
import judgels.user.UserStore;
import judgels.user.info.UserInfoStore;
import tlx.TlxScope;
import tlx.auth.google.GoogleAuth;
import tlx.recaptcha.RecaptchaVerifier;
import tlx.user.registration.web.UserRegistrationWebConfig;

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
    @TlxScope
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
