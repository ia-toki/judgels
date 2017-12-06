package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.session.SessionResource;
import judgels.jophiel.user.UserResource;
import judgels.jophiel.user.master.MasterUsersCreator;
import judgels.jophiel.user.master.MasterUsersModule;

@Component(modules = {
        JophielModule.class,
        JophielHibernateModule.class,
        MailerModule.class,
        MasterUsersModule.class})
@Singleton
public interface JophielComponent {
    MasterUsersCreator masterUsersCreator();

    SessionResource accountResource();
    UserResource userResource();
    VersionResource versionResource();
}
