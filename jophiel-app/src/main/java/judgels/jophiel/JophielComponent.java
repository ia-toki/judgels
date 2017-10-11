package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.user.UserResource;

@Component(modules = {
        JophielHibernateModule.class})
@Singleton
public interface JophielComponent {
    VersionResource versionResource();
    UserResource userResource();
}
