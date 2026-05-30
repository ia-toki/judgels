package tlx.user.account;
import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import judgels.user.UserStore;
import tlx.TlxScope;
import tlx.mailer.Mailer;

@Module
public class UserResetPasswordModule {
    private final UserResetPasswordConfiguration config;

    public UserResetPasswordModule(UserResetPasswordConfiguration config) {
        this.config = config;
    }

    @Provides
    @TlxScope
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
