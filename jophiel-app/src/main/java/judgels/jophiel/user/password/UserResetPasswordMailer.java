package judgels.jophiel.user.password;

import judgels.jophiel.api.user.User;
import judgels.jophiel.mailer.Mailer;

public class UserResetPasswordMailer {
    private final Mailer mailer;

    public UserResetPasswordMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public void sendRequestEmail(User user, String emailCode) {
        mailer.send(user.getEmail(), "Request to Reset Password", emailCode);
    }

    public void sendResetEmail(User user) {
        mailer.send(user.getEmail(), "Your password has been reset", "Reset");
    }
}
