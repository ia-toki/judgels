package judgels.training.account.password;

import java.time.Duration;
import judgels.api.training.account.password.AccountPasswordResetData;
import judgels.api.user.User;
import judgels.user.UserStore;

public class AccountPasswordResetter {
    private static final Duration FORGOT_PASSWORD_EXPIRATION = Duration.ofHours(1);

    private final UserStore userStore;
    private final AccountResetPasswordStore accountResetPasswordStore;
    private final AccountResetPasswordMailer accountResetPasswordMailer;

    public AccountPasswordResetter(
            UserStore userStore,
            AccountResetPasswordStore accountResetPasswordStore,
            AccountResetPasswordMailer accountResetPasswordMailer) {

        this.userStore = userStore;
        this.accountResetPasswordStore = accountResetPasswordStore;
        this.accountResetPasswordMailer = accountResetPasswordMailer;
    }

    public void request(User user, String email) {
        String emailCode = accountResetPasswordStore.generateEmailCode(user.getJid(), FORGOT_PASSWORD_EXPIRATION);
        accountResetPasswordMailer.sendRequestEmail(user, email, emailCode);
    }

    public void reset(AccountPasswordResetData data) {
        String emailCode = data.getEmailCode();

        String userJid = accountResetPasswordStore.consumeEmailCode(emailCode, FORGOT_PASSWORD_EXPIRATION)
                .orElseThrow(IllegalArgumentException::new);
        User user = userStore.getUserByJid(userJid)
                .orElseThrow(IllegalStateException::new);
        String email = userStore.getUserEmailByJid(userJid)
                .orElseThrow(IllegalStateException::new);

        userStore.updateUserPassword(user.getJid(), data.getNewPassword());

        accountResetPasswordMailer.sendResetEmail(user, email);
    }
}
