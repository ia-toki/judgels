package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.account.SessionResource;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.user.UserResource;

@Component(modules = {
        JophielModule.class,
        JophielHibernateModule.class,
        MailerModule.class})
@Singleton
public interface JophielComponent {
    SessionResource accountResource();
    UserResource userResource();
    VersionResource versionResource();
}
