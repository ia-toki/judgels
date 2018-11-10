package judgels.jophiel.user.account;

import judgels.jophiel.EmailTemplate;
import judgels.jophiel.api.user.User;
import judgels.jophiel.mailer.Mailer;

public class UserResetPasswordMailer {
    private final Mailer mailer;
    private final EmailTemplate requestEmailTemplate;
    private final EmailTemplate resetEmailTemplate;

    public UserResetPasswordMailer(
            Mailer mailer,
            EmailTemplate requestEmailTemplate,
            EmailTemplate resetEmailTemplate) {

        this.mailer = mailer;
        this.requestEmailTemplate = requestEmailTemplate;
        this.resetEmailTemplate = resetEmailTemplate;
    }

    public void sendRequestEmail(User user, String email, String emailCode) {
        String subject = requestEmailTemplate.getSubject();
        String body = requestEmailTemplate.getBody()
                .replace("{{username}}", user.getUsername())
                .replace("{{emailCode}}", emailCode);
        mailer.send(email, subject, body);
    }

    public void sendResetEmail(User user, String email) {
        String subject = resetEmailTemplate.getSubject();
        String body = resetEmailTemplate.getBody()
                .replace("{{username}}", user.getUsername());
        mailer.send(email, subject, body);
    }
}
