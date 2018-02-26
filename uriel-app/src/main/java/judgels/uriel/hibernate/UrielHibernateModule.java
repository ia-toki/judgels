package judgels.uriel.hibernate;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.time.Clock;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.uriel.contest.ContestContestantDao;
import judgels.uriel.contest.ContestDao;
import judgels.uriel.contest.ContestScoreboardDao;
import org.hibernate.SessionFactory;

@Module
public class UrielHibernateModule {
    private final HibernateBundle<?> hibernateBundle;
    private final SessionFactory sessionFactory;

    public UrielHibernateModule(HibernateBundle<?> hibernateBundle) {
        this.hibernateBundle = hibernateBundle;
        this.sessionFactory = hibernateBundle.getSessionFactory();
    }

    @Provides
    @Singleton
    SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Provides
    @Singleton
    UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory() {
        return new UnitOfWorkAwareProxyFactory(hibernateBundle);
    }

    @Provides
    @Singleton
    ContestDao contestDao(Clock clock, ActorProvider actorProvider) {
        return new ContestHibernateDao(sessionFactory, clock, actorProvider);
    }

    @Provides
    @Singleton
    ContestScoreboardDao contestScoreboardDao(Clock clock, ActorProvider actorProvider) {
        return new ContestScoreboardHibernateDao(sessionFactory, clock, actorProvider);
    }

    @Provides
    @Singleton
    ContestContestantDao contestContestantDao(Clock clock, ActorProvider actorProvider) {
        return new ContestContestantHibernateDao(sessionFactory, clock, actorProvider);
    }

}
