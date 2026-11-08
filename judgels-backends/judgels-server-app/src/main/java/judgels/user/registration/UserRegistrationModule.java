package judgels.user.registration;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.core.auth.google.GoogleAuth;
import judgels.core.mailer.Mailer;
import judgels.core.recaptcha.RecaptchaVerifier;
import judgels.core.recaptcha.web.RecaptchaWebConfig;
import judgels.setting.SettingStore;
import judgels.user.UserStore;
import judgels.user.info.UserInfoStore;

@Module
public class UserRegistrationModule {
    private final Optional<RecaptchaWebConfig> recaptchaWebConfig;

    public UserRegistrationModule(Optional<RecaptchaWebConfig> recaptchaWebConfig) {
        this.recaptchaWebConfig = recaptchaWebConfig;
    }

    @Provides
    Optional<RecaptchaWebConfig> recaptchaWebConfig() {
        return recaptchaWebConfig;
    }

    @Provides
    @Singleton
    UserRegisterer userRegisterer(
            UserStore userStore,
            UserInfoStore userInfoStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            SettingStore settingStore,
            Optional<Mailer> mailer,
            Optional<RecaptchaVerifier> recaptchaVerifier,
            Optional<GoogleAuth> googleAuth) {

        return new UserRegisterer(
                userStore,
                userInfoStore,
                userRegistrationEmailStore,
                new UserRegistrationEmailMailer(mailer, settingStore),
                settingStore,
                mailer,
                recaptchaVerifier,
                googleAuth);
    }
}
