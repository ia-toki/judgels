package judgels.uriel.hibernate;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.uriel.contest.ContestDao;
import judgels.uriel.contest.contestant.ContestContestantDao;
import judgels.uriel.contest.scoreboard.ContestScoreboardDao;
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
    SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Provides
    @Singleton
    UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory() {
        return new UnitOfWorkAwareProxyFactory(hibernateBundle);
    }

    @Provides
    ContestDao contestDao(ContestHibernateDao dao) {
        return dao;
    }

    @Provides
    ContestScoreboardDao contestScoreboardDao(ContestScoreboardHibernateDao dao) {
        return dao;
    }

    @Provides
    ContestContestantDao contestContestantDao(ContestContestantHibernateDao dao) {
        return dao;
    }
}
