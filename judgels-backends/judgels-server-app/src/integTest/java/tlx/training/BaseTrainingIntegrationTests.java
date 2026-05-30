package tlx.training;

import java.time.Clock;
import judgels.persistence.TestActorProvider;
import judgels.service.persistence.JudgelsPersistenceModule;
import judgels.service.persistence.hibernate.JudgelsHibernateModule;
import org.hibernate.SessionFactory;

public abstract class BaseTrainingIntegrationTests {
    protected static TrainingIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return DaggerTrainingIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(Clock.systemUTC(), new TestActorProvider()))
                .build();
    }
}
