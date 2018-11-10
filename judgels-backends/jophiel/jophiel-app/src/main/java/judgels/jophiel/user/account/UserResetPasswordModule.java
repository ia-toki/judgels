package judgels.jophiel.user.account;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.mailer.Mailer;
import judgels.jophiel.user.UserStore;

@Module
public class UserResetPasswordModule {
    private final UserResetPasswordConfiguration config;

    public UserResetPasswordModule(UserResetPasswordConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    Optional<UserPasswordResetter> userPasswordResetter(
            UserStore userStore,
            UserResetPasswordStore userResetPasswordStore,
            Optional<Mailer> mailer) {

        if (!config.getEnabled()) {
            return Optional.empty();
        }

        return Optional.of(new UserPasswordResetter(
                userStore,
                userResetPasswordStore,
                new UserResetPasswordMailer(
                        mailer.get(),
                        config.getRequestEmailTemplate(),
                        config.getResetEmailTemplate())));
    }
}
