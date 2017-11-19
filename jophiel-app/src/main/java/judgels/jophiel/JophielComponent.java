package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.account.AccountResource;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.user.UserResource;

@Component(modules = {
        JophielModule.class,
        JophielHibernateModule.class})
@Singleton
public interface JophielComponent {
    AccountResource accountResource();
    UserResource userResource();
    VersionResource versionResource();
}
