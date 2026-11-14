package judgels.training.account.password;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.core.mailer.Mailer;
import judgels.setting.SettingStore;
import judgels.user.UserStore;

@Module
public class AccountResetPasswordModule {
    @Provides
    @Singleton
    AccountPasswordResetter accountPasswordResetter(
            UserStore userStore,
            AccountResetPasswordStore accountResetPasswordStore,
            SettingStore settingStore,
            Optional<Mailer> mailer) {

        return new AccountPasswordResetter(
                userStore,
                accountResetPasswordStore,
                new AccountResetPasswordMailer(mailer, settingStore));
    }
}
