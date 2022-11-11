package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.jophiel.JophielModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JophielModule.class,
        UrielModule.class,
        UrielHibernateDaoModule.class})
@Singleton
public interface UrielIntegrationTestComponent {
    ContestStore contestStore();
}
