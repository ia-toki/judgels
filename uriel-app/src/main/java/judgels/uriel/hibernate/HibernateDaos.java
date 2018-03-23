package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.Daos.AdminRoleDao;
import judgels.uriel.persistence.Daos.ContestContestantDao;
import judgels.uriel.persistence.Daos.ContestDao;
import judgels.uriel.persistence.Daos.ContestScoreboardDao;
import org.hibernate.SessionFactory;

public class HibernateDaos {
    private HibernateDaos() {}

    @Singleton
    public static class AdminRoleHibernateDao
            extends UnmodifiableHibernateDao<AdminRoleModel>
            implements AdminRoleDao {

        @Inject
        public AdminRoleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }
    }

    @Singleton
    public static class ContestHibernateDao
            extends JudgelsHibernateDao<ContestModel>
            implements ContestDao {

        @Inject
        public ContestHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }
    }

    @Singleton
    public static class ContestContestantHibernateDao
            extends HibernateDao<ContestContestantModel>
            implements ContestContestantDao {

        @Inject
        public ContestContestantHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }
    }

    @Singleton
    public static class ContestScoreboardHibernateDao
            extends HibernateDao<ContestScoreboardModel>
            implements ContestScoreboardDao {

        @Inject
        public ContestScoreboardHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }
    }
}
