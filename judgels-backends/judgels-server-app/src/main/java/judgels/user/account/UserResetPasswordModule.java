package judgels.user.account;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.core.mailer.Mailer;
import judgels.setting.SettingStore;
import judgels.user.UserStore;

@Module
public class UserResetPasswordModule {
    @Provides
    @Singleton
    UserPasswordResetter userPasswordResetter(
            UserStore userStore,
            UserResetPasswordStore userResetPasswordStore,
            SettingStore settingStore,
            Optional<Mailer> mailer) {

        return new UserPasswordResetter(
                userStore,
                userResetPasswordStore,
                new UserResetPasswordMailer(mailer, settingStore),
                settingStore,
                mailer);
    }
}
