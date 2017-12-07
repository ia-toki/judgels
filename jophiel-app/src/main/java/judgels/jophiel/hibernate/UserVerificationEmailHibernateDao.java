package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import judgels.jophiel.user.email.UserVerificationEmailDao;
import judgels.jophiel.user.email.UserVerificationEmailModel;
import judgels.jophiel.user.email.UserVerificationEmailModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

public class UserVerificationEmailHibernateDao extends HibernateDao<UserVerificationEmailModel>
        implements UserVerificationEmailDao {

    public UserVerificationEmailHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserVerificationEmailModel> findByUserJid(String userJid) {
        return selectByUniqueColumn(UserVerificationEmailModel_.userJid, userJid);
    }

    @Override
    public Optional<UserVerificationEmailModel> findByEmailCode(String emailCode) {
        return selectByUniqueColumn(UserVerificationEmailModel_.emailCode, emailCode);
    }
}
