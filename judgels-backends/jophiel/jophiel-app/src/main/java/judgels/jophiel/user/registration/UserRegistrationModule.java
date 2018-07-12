package judgels.jophiel.user.registration;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.mailer.Mailer;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.profile.UserProfileStore;
import judgels.recaptcha.RecaptchaVerifier;

@Module
public class UserRegistrationModule {
    private final UserRegistrationConfiguration config;

    public UserRegistrationModule(UserRegistrationConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    Optional<UserRegisterer> userRegisterer(
            UserStore userStore,
            UserProfileStore userProfileStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            Optional<Mailer> mailer,
            Optional<RecaptchaVerifier> recaptchaVerifier) {

        if (!config.getEnabled()) {
            return Optional.empty();
        }

        Optional<RecaptchaVerifier> actualRecaptchaVerifier = recaptchaVerifier;
        if (!config.getUseRecaptcha()) {
            actualRecaptchaVerifier = Optional.empty();
        }

        return Optional.of(new UserRegisterer(
                userStore,
                userProfileStore,
                userRegistrationEmailStore,
                new UserRegistrationEmailMailer(mailer.get(), config.getActivationEmailTemplate()),
                actualRecaptchaVerifier));
    }
}
