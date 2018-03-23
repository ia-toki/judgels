package judgels.uriel.hibernate;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.uriel.hibernate.HibernateDaos.AdminRoleHibernateDao;
import judgels.uriel.hibernate.HibernateDaos.ContestContestantHibernateDao;
import judgels.uriel.hibernate.HibernateDaos.ContestHibernateDao;
import judgels.uriel.hibernate.HibernateDaos.ContestScoreboardHibernateDao;
import judgels.uriel.persistence.ContestRawDao;
import judgels.uriel.persistence.Daos.AdminRoleDao;
import judgels.uriel.persistence.Daos.ContestContestantDao;
import judgels.uriel.persistence.Daos.ContestDao;
import judgels.uriel.persistence.Daos.ContestScoreboardDao;
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
    AdminRoleDao adminRoleDao(AdminRoleHibernateDao dao) {
        return dao;
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

    @Provides
    ContestRawDao contestRawDao(ContestRawHibernateDao dao) {
        return dao;
    }
}
