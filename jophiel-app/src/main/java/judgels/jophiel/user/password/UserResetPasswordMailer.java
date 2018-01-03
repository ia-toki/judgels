package judgels.jophiel.user.password;

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

    public void sendRequestEmail(User user, String emailCode) {
        String subject = requestEmailTemplate.getSubject();
        String body = requestEmailTemplate.getBody()
                .replace("{{username}}", user.getUsername())
                .replace("{{emailCode}}", emailCode);
        mailer.send(user.getEmail(), subject, body);
    }

    public void sendResetEmail(User user) {
        String subject = resetEmailTemplate.getSubject();
        String body = resetEmailTemplate.getBody()
                .replace("{{username}}", user.getUsername());
        mailer.send(user.getEmail(), subject, body);
    }
}
