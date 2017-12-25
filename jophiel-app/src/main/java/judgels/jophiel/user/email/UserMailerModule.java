package judgels.jophiel.user.email;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.mailer.Mailer;
import judgels.jophiel.user.password.UserForgotPasswordMailer;
import judgels.jophiel.user.registration.UserRegistrationEmailMailer;

@Module
public class UserMailerModule {
    private UserMailerModule() {}

    @Provides
    @Singleton
    static Optional<UserRegistrationEmailMailer> userRegistrationEmailMailer(Optional<Mailer> mailer) {
        return mailer.map(UserRegistrationEmailMailer::new);
    }

    @Provides
    @Singleton
    static Optional<UserForgotPasswordMailer> userForgotPasswordMailer(Optional<Mailer> mailer) {
        return mailer.map(UserForgotPasswordMailer::new);
    }
}
