package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.session.SessionResource;
import judgels.jophiel.user.UserResource;
import judgels.jophiel.user.password.UserResetPasswordModule;
import judgels.jophiel.user.registration.UserRegistrationModule;
import judgels.jophiel.user.superadmin.SuperadminCreator;
import judgels.jophiel.user.superadmin.SuperadminModule;
import judgels.jophiel.web.WebModule;
import judgels.jophiel.web.WebResource;
import judgels.recaptcha.RecaptchaModule;

@Component(modules = {
        JophielModule.class,
        JophielHibernateModule.class,
        MailerModule.class,
        RecaptchaModule.class,
        SuperadminModule.class,
        UserRegistrationModule.class,
        UserResetPasswordModule.class,
        WebModule.class})
@Singleton
public interface JophielComponent {
    SuperadminCreator superadminCreator();

    SessionResource accountResource();
    UserResource userResource();
    VersionResource versionResource();
    WebResource webResource();
}
