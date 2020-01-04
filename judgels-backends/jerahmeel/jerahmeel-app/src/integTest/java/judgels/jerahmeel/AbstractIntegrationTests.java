package judgels.jerahmeel;

import java.time.Clock;
import judgels.persistence.TestActorProvider;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import org.hibernate.SessionFactory;

public abstract class AbstractIntegrationTests {
    protected static JerahmeelIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return DaggerJerahmeelIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(Clock.systemUTC(), new TestActorProvider()))
                .build();
    }
}
