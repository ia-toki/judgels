package judgels.jophiel.user.email;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.mailer.Mailer;

@Module
public class UserMailerModule {
    private UserMailerModule() {}

    @Provides
    @Singleton
    static Optional<UserVerificationEmailMailer> userVerificationEmailMailer(Optional<Mailer> mailer) {
        return mailer.map(UserVerificationEmailMailer::new);
    }
}
