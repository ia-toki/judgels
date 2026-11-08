package judgels.user.registration;

import java.util.Optional;
import judgels.api.setting.EmailTemplate;
import judgels.api.user.User;
import judgels.core.mailer.Mailer;
import judgels.setting.SettingStore;

public class UserRegistrationEmailMailer {
    private final Optional<Mailer> mailer;
    private final SettingStore settingStore;

    public UserRegistrationEmailMailer(Optional<Mailer> mailer, SettingStore settingStore) {
        this.mailer = mailer;
        this.settingStore = settingStore;
    }

    public void sendActivationEmail(User user, String email, String emailCode) {
        EmailTemplate template = settingStore.getSettings().getUserRegistration().getActivationEmailTemplate();
        String subject = template.getSubject();
        String body = template.getBody()
                .replace("{{username}}", user.getUsername())
                .replace("{{emailCode}}", emailCode);
        mailer.orElseThrow().send(email, subject, body);
    }
}
