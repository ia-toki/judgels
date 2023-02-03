package judgels.jophiel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRegistrationEmailDao;
import judgels.jophiel.persistence.UserRegistrationEmailModel;
import judgels.jophiel.persistence.UserRegistrationEmailModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

@Singleton
public class UserRegistrationEmailHibernateDao extends HibernateDao<UserRegistrationEmailModel>
        implements UserRegistrationEmailDao {

    @Inject
    public UserRegistrationEmailHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<UserRegistrationEmailModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(UserRegistrationEmailModel_.userJid, userJid);
    }

    @Override
    public Optional<UserRegistrationEmailModel> selectByEmailCode(String emailCode) {
        return selectByUniqueColumn(UserRegistrationEmailModel_.emailCode, emailCode);
    }
}
