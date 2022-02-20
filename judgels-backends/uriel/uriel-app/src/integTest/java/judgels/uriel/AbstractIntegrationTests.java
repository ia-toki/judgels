package judgels.uriel;

import java.time.Clock;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.persistence.ActorProvider;
import judgels.persistence.TestActorProvider;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.uriel.jophiel.JophielModule;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractIntegrationTests {
    protected static UrielIntegrationTestComponent createComponent(SessionFactory sessionFactory) {
        return createComponent(sessionFactory, Clock.systemUTC(), new TestActorProvider());
    }

    protected static UrielIntegrationTestComponent createComponent(
            SessionFactory sessionFactory,
            Clock clock,
            ActorProvider actorProvider) {

        return DaggerUrielIntegrationTestComponent.builder()
                .judgelsHibernateModule(new JudgelsHibernateModule(sessionFactory))
                .judgelsPersistenceModule(new JudgelsPersistenceModule(clock, actorProvider))
                .jophielModule(new JophielModule(JophielClientConfiguration.DEFAULT))
                .build();
    }

    @BeforeEach
    void before() {
        UrielCacheUtils.removeDurations();
    }
}
