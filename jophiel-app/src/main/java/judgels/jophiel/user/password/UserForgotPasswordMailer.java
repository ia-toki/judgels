package judgels.jophiel.user.password;

import judgels.jophiel.api.user.User;
import judgels.jophiel.mailer.Mailer;

public class UserForgotPasswordMailer {
    private final Mailer mailer;

    public UserForgotPasswordMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public void sendRequestEmail(User user, String emailCode) {
        mailer.send(user.getEmail(), "Request to Reset Password", emailCode);
    }
}
