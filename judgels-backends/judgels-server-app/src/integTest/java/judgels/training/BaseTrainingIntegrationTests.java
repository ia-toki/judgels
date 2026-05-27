package judgels.training;

import java.time.Clock;
import judgels.persistence.JudgelsPersistenceModule;
import judgels.persistence.TestActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateModule;
import org.hibernate.SessionFactory;

public abstract class BaseTrainingIntegrationTests {
    protected static TrainingIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return DaggerTrainingIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(Clock.systemUTC(), new TestActorProvider()))
                .build();
    }
}
