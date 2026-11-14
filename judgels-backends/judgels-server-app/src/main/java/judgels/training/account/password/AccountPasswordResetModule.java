package judgels.training.account.password;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.core.mailer.Mailer;
import judgels.setting.SettingStore;
import judgels.user.UserStore;

@Module
public class AccountPasswordResetModule {
    @Provides
    @Singleton
    AccountPasswordResetter accountPasswordResetter(
            UserStore userStore,
            AccountPasswordResetStore accountPasswordResetStore,
            SettingStore settingStore,
            Optional<Mailer> mailer) {

        return new AccountPasswordResetter(
                userStore,
                accountPasswordResetStore,
                new AccountPasswordResetMailer(mailer, settingStore));
    }
}
