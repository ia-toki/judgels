package tlx.user.account;

import java.time.Duration;
import judgels.api.user.User;
import judgels.api.user.account.PasswordResetData;
import judgels.user.UserStore;

public class UserPasswordResetter {
    private static final Duration FORGOT_PASSWORD_EXPIRATION = Duration.ofHours(1);

    private final UserStore userStore;
    private final UserResetPasswordStore userResetPasswordStore;
    private final UserResetPasswordMailer userResetPasswordMailer;

    public UserPasswordResetter(
            UserStore userStore,
            UserResetPasswordStore userResetPasswordStore,
            UserResetPasswordMailer userResetPasswordMailer) {

        this.userStore = userStore;
        this.userResetPasswordStore = userResetPasswordStore;
        this.userResetPasswordMailer = userResetPasswordMailer;
    }

    public void request(User user, String email) {
        String emailCode = userResetPasswordStore.generateEmailCode(user.getJid(), FORGOT_PASSWORD_EXPIRATION);
        userResetPasswordMailer.sendRequestEmail(user, email, emailCode);
    }

    public void reset(PasswordResetData data) {
        String emailCode = data.getEmailCode();

        String userJid = userResetPasswordStore.consumeEmailCode(emailCode, FORGOT_PASSWORD_EXPIRATION)
                .orElseThrow(IllegalArgumentException::new);
        User user = userStore.getUserByJid(userJid)
                .orElseThrow(IllegalStateException::new);
        String email = userStore.getUserEmailByJid(userJid)
                .orElseThrow(IllegalStateException::new);

        userStore.updateUserPassword(user.getJid(), data.getNewPassword());

        userResetPasswordMailer.sendResetEmail(user, email);
    }
}
