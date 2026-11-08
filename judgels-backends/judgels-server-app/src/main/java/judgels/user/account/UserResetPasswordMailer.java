package judgels.user.account;

import java.util.Optional;
import judgels.api.setting.EmailTemplate;
import judgels.api.setting.UserResetPasswordSettings;
import judgels.api.user.User;
import judgels.core.mailer.Mailer;
import judgels.setting.SettingStore;

public class UserResetPasswordMailer {
    private final Optional<Mailer> mailer;
    private final SettingStore settingStore;

    public UserResetPasswordMailer(Optional<Mailer> mailer, SettingStore settingStore) {
        this.mailer = mailer;
        this.settingStore = settingStore;
    }

    public void sendRequestEmail(User user, String email, String emailCode) {
        EmailTemplate template = getSettings().getRequestEmailTemplate();
        String subject = template.getSubject();
        String body = template.getBody()
                .replace("{{username}}", user.getUsername())
                .replace("{{emailCode}}", emailCode);
        mailer.orElseThrow().send(email, subject, body);
    }

    public void sendResetEmail(User user, String email) {
        EmailTemplate template = getSettings().getResetEmailTemplate();
        String subject = template.getSubject();
        String body = template.getBody()
                .replace("{{username}}", user.getUsername());
        mailer.orElseThrow().send(email, subject, body);
    }

    private UserResetPasswordSettings getSettings() {
        return settingStore.getSettings().getUserResetPassword();
    }
}
