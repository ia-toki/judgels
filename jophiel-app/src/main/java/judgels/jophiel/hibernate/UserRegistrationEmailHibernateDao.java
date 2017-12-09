package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import judgels.jophiel.user.email.UserRegistrationEmailDao;
import judgels.jophiel.user.email.UserRegistrationEmailModel;
import judgels.jophiel.user.email.UserRegistrationEmailModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

public class UserRegistrationEmailHibernateDao extends HibernateDao<UserRegistrationEmailModel>
        implements UserRegistrationEmailDao {

    public UserRegistrationEmailHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserRegistrationEmailModel> findByUserJid(String userJid) {
        return selectByUniqueColumn(UserRegistrationEmailModel_.userJid, userJid);
    }

    @Override
    public Optional<UserRegistrationEmailModel> findByEmailCode(String emailCode) {
        return selectByUniqueColumn(UserRegistrationEmailModel_.emailCode, emailCode);
    }
}
