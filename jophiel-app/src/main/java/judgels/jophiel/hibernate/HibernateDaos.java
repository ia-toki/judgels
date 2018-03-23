package judgels.jophiel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.Daos.UserProfileDao;
import judgels.jophiel.persistence.UserProfileModel;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

public class HibernateDaos {
    private HibernateDaos() {}

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
