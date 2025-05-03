package judgels.jophiel.hibernate;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.jophiel.persistence.UserRegistrationEmailDao;
import judgels.jophiel.persistence.UserRegistrationEmailModel;
import judgels.jophiel.persistence.UserRegistrationEmailModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class UserRegistrationEmailHibernateDao extends HibernateDao<UserRegistrationEmailModel> implements UserRegistrationEmailDao {
    @Inject
    public UserRegistrationEmailHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<UserRegistrationEmailModel> selectByUserJid(String userJid) {
        return select().where(columnEq(UserRegistrationEmailModel_.userJid, userJid)).unique();
    }

    @Override
    public Optional<UserRegistrationEmailModel> selectByEmailCode(String emailCode) {
        return select().where(columnEq(UserRegistrationEmailModel_.emailCode, emailCode)).unique();
    }
}
