package judgels.user;

import java.time.Clock;
import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.service.persistence.hibernate.JudgelsHibernateModule;
import org.hibernate.SessionFactory;

public abstract class BaseUserIntegrationTests {
    protected static UserIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return createComponent(sessionFactory, new TestClock());
    }

    protected static UserIntegrationTestComponent createComponent(SessionFactory sessionFactory, Clock clock) {
        return DaggerUserIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(clock, new TestActorProvider()))
                .build();
    }
}
