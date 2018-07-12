package judgels.jophiel.mailer;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class Mailer {
    private MailerConfiguration config;

    public Mailer(MailerConfiguration config) {
        this.config = config;
    }

    public void send(String recipient, String subject, String body) {
        new Thread(() -> {
            try {
                HtmlEmail email = new HtmlEmail();
                email.setHostName(config.getHost());
                email.setSmtpPort(config.getPort());
                email.setAuthenticator(new DefaultAuthenticator(config.getUsername(), config.getPassword()));
                email.setSSLOnConnect(config.getUseSsl());
                email.setFrom(config.getSender());
                email.setSubject(subject);
                email.setHtmlMsg(body);
                email.addTo(recipient);
                email.send();
            } catch (EmailException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
