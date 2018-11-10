package judgels.jophiel.user.account;

import judgels.jophiel.EmailTemplate;
import judgels.jophiel.api.user.User;
import judgels.jophiel.mailer.Mailer;

public class UserRegistrationEmailMailer {
    private final Mailer mailer;
    private final EmailTemplate activationEmailTemplate;

    public UserRegistrationEmailMailer(Mailer mailer, EmailTemplate activationEmailTemplate) {
        this.mailer = mailer;
        this.activationEmailTemplate = activationEmailTemplate;
    }

    public void sendActivationEmail(User user, String email, String emailCode) {
        String subject = activationEmailTemplate.getSubject();
        String body = activationEmailTemplate.getBody()
                .replace("{{username}}", user.getUsername())
                .replace("{{emailCode}}", emailCode);
        mailer.send(email, subject, body);
    }
}
