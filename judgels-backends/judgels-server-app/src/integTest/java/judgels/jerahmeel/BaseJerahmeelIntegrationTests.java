package judgels.jerahmeel;

import java.time.Clock;
import judgels.persistence.TestActorProvider;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import org.hibernate.SessionFactory;

public abstract class BaseJerahmeelIntegrationTests {
    protected static JerahmeelIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return DaggerJerahmeelIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(Clock.systemUTC(), new TestActorProvider()))
                .build();
    }
}
