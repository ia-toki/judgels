package judgels.uriel;

import java.time.Clock;
import judgels.persistence.ActorProvider;
import judgels.persistence.TestActorProvider;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractIntegrationTests {
    protected static UrielIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return createComponent(sessionFactory, Clock.systemUTC(), new TestActorProvider());
    }

    protected static UrielIntegrationTestComponent createComponent(
            SessionFactory sessionFactory,
            ActorProvider actorProvider) {

        return createComponent(sessionFactory, Clock.systemUTC(), actorProvider);
    }

    protected static UrielIntegrationTestComponent createComponent(SessionFactory sessionFactory, Clock clock) {
        return createComponent(sessionFactory, clock, new TestActorProvider());
    }

    protected static UrielIntegrationTestComponent createComponent(
            SessionFactory sessionFactory,
            Clock clock,
            ActorProvider actorProvider) {

        return DaggerUrielIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(clock, actorProvider))
                .build();
    }

    @BeforeEach
    void before() {
        UrielCacheUtils.removeDurations();
    }
}
