package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.Daos.AdminRoleDao;
import judgels.uriel.persistence.Daos.ContestDao;
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
}
