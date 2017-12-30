package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.session.SessionResource;
import judgels.jophiel.user.UserResource;
import judgels.jophiel.user.email.UserMailerModule;
import judgels.jophiel.user.master.MasterUsersCreator;
import judgels.jophiel.user.master.MasterUsersModule;
import judgels.jophiel.web.WebModule;
import judgels.jophiel.web.WebResource;

@Component(modules = {
        JophielModule.class,
        JophielHibernateModule.class,
        MailerModule.class,
        MasterUsersModule.class,
        UserMailerModule.class,
        WebModule.class})
@Singleton
public interface JophielComponent {
    MasterUsersCreator masterUsersCreator();

    SessionResource accountResource();
    UserResource userResource();
    WebResource webResource();
    VersionResource versionResource();
}
