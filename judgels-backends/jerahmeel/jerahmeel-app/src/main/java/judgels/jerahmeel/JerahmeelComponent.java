package judgels.jerahmeel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.jerahmeel.admin.AdminResource;
import judgels.jerahmeel.hibernate.JerahmeelHibernateDaoModule;
import judgels.jerahmeel.jophiel.JophielModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;

@Component(modules = {
        JerahmeelHibernateDaoModule.class,
        JerahmeelModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JophielModule.class
})
@Singleton
public interface JerahmeelComponent {
    AdminResource adminResource();
    VersionResource versionResource();
}
