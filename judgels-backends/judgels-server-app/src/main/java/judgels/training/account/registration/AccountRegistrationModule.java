package judgels.training.account.registration;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.core.auth.google.GoogleAuth;
import judgels.core.mailer.Mailer;
import judgels.core.recaptcha.RecaptchaVerifier;
import judgels.setting.SettingStore;
import judgels.setting.TrainingAccountChecker;
import judgels.user.UserStore;
import judgels.user.info.UserInfoStore;

@Module
public class AccountRegistrationModule {
    @Provides
    @Singleton
    AccountRegisterer accountRegisterer(
            UserStore userStore,
            UserInfoStore userInfoStore,
            AccountRegistrationEmailStore accountRegistrationEmailStore,
            SettingStore settingStore,
            TrainingAccountChecker trainingAccountChecker,
            Optional<Mailer> mailer,
            Optional<RecaptchaVerifier> recaptchaVerifier,
            Optional<GoogleAuth> googleAuth) {

        return new AccountRegisterer(
                userStore,
                userInfoStore,
                accountRegistrationEmailStore,
                new AccountRegistrationEmailMailer(mailer, settingStore),
                trainingAccountChecker,
                recaptchaVerifier,
                googleAuth);
    }
}
