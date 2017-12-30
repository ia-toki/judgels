package judgels.jophiel.user.password;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.mailer.Mailer;
import judgels.jophiel.user.UserStore;

@Module
public class UserResetPasswordModule {
    private UserResetPasswordModule() {}

    @Provides
    @Singleton
    static UserPasswordResetter userPasswordResetter(
            UserStore userStore,
            UserResetPasswordStore userResetPasswordStore,
            Optional<Mailer> mailer) {

        return new UserPasswordResetter(userStore, userResetPasswordStore, new UserResetPasswordMailer(mailer.get()));
    }
}
