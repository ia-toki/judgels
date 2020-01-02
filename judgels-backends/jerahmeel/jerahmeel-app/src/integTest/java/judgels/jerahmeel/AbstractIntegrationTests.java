package judgels.jerahmeel;

import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import org.hibernate.SessionFactory;

public abstract class AbstractIntegrationTests {
    protected static JerahmeelIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return DaggerJerahmeelIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(new TestClock(), new TestActorProvider()))
                .build();
    }
}
