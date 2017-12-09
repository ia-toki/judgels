package judgels.jophiel.user.email;

import judgels.jophiel.api.user.User;
import judgels.jophiel.mailer.Mailer;

public class UserRegistrationEmailMailer {
    private final Mailer mailer;

    public UserRegistrationEmailMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public void sendVerificationEmail(User user, String emailCode) {
        mailer.send(user.getEmail(), "Verify Your Email", emailCode);
    }
}
