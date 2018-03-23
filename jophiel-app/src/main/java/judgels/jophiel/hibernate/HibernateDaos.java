package judgels.jophiel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.Daos.SessionDao;
import judgels.jophiel.persistence.Daos.UserProfileDao;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.UserProfileModel;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

public class HibernateDaos {
    private HibernateDaos() {}

    @Singleton
    public static class SessionHibernateDao
            extends UnmodifiableHibernateDao<SessionModel>
            implements SessionDao {

        @Inject
        public SessionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }
    }

    @Singleton
    public static class UserProfileHibernateDao
            extends HibernateDao<UserProfileModel>
            implements UserProfileDao {

        @Inject
        public UserProfileHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }
    }
}
