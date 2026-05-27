package judgels.training;

import java.time.Clock;
import judgels.persistence.TestActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateModule;
import judgels.service.persistence.JudgelsPersistenceModule;
import org.hibernate.SessionFactory;

public abstract class BaseTrainingIntegrationTests {
    protected static TrainingIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return DaggerTrainingIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(Clock.systemUTC(), new TestActorProvider()))
                .build();
    }
}
