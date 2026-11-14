package judgels.training.account.password;

import java.time.Duration;
import judgels.api.training.account.password.AccountPasswordResetData;
import judgels.api.user.User;
import judgels.user.UserStore;

public class AccountPasswordResetter {
    private static final Duration FORGOT_PASSWORD_EXPIRATION = Duration.ofHours(1);

    private final UserStore userStore;
    private final AccountPasswordResetStore accountPasswordResetStore;
    private final AccountPasswordResetMailer accountPasswordResetMailer;

    public AccountPasswordResetter(
            UserStore userStore,
            AccountPasswordResetStore accountPasswordResetStore,
            AccountPasswordResetMailer accountPasswordResetMailer) {

        this.userStore = userStore;
        this.accountPasswordResetStore = accountPasswordResetStore;
        this.accountPasswordResetMailer = accountPasswordResetMailer;
    }

    public void request(User user, String email) {
        String emailCode = accountPasswordResetStore.generateEmailCode(user.getJid(), FORGOT_PASSWORD_EXPIRATION);
        accountPasswordResetMailer.sendRequestEmail(user, email, emailCode);
    }

    public void reset(AccountPasswordResetData data) {
        String emailCode = data.getEmailCode();

        String userJid = accountPasswordResetStore.consumeEmailCode(emailCode, FORGOT_PASSWORD_EXPIRATION)
                .orElseThrow(IllegalArgumentException::new);
        User user = userStore.getUserByJid(userJid)
                .orElseThrow(IllegalStateException::new);
        String email = userStore.getUserEmailByJid(userJid)
                .orElseThrow(IllegalStateException::new);

        userStore.updateUserPassword(user.getJid(), data.getNewPassword());

        accountPasswordResetMailer.sendResetEmail(user, email);
    }
}
