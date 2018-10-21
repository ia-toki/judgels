package judgels.uriel;

import judgels.persistence.ActorProvider;
import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import org.hibernate.SessionFactory;

public abstract class AbstractIntegrationTests {
    protected static UrielIntegrationTestComponent createComponent(
            SessionFactory sessionFactory) {
        return createComponent(sessionFactory, new TestActorProvider());
    }

    protected static UrielIntegrationTestComponent createComponent(
            SessionFactory sessionFactory,
            ActorProvider actorProvider) {

        return DaggerUrielIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(new TestClock(), actorProvider))
                .build();
    }
}
