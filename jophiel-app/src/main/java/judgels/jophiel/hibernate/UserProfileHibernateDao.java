package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import judgels.jophiel.user.profile.UserProfileDao;
import judgels.jophiel.user.profile.UserProfileModel;
import judgels.jophiel.user.profile.UserProfileModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

public class UserProfileHibernateDao extends HibernateDao<UserProfileModel> implements UserProfileDao {
    public UserProfileHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserProfileModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(UserProfileModel_.userJid, userJid);
    }
}
