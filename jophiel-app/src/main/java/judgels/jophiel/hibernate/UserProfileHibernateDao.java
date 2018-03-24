package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserProfileDao;
import judgels.jophiel.persistence.UserProfileModel;
import judgels.jophiel.persistence.UserProfileModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class UserProfileHibernateDao extends HibernateDao<UserProfileModel> implements UserProfileDao {
    @Inject
    public UserProfileHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserProfileModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(UserProfileModel_.userJid, userJid);
    }
}
