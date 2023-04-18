package judgels.jophiel;

import java.time.Clock;
import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import org.hibernate.SessionFactory;

public abstract class BaseJophielIntegrationTests {
    protected static JophielIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return createComponent(sessionFactory, new TestClock());
    }

    protected static JophielIntegrationTestComponent createComponent(SessionFactory sessionFactory, Clock clock) {
        return DaggerJophielIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(clock, new TestActorProvider()))
                .build();
    }
}
