package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.UserRegistrationEmailDao;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.UserRegistrationEmailModel;
import judgels.persistence.model.UserRegistrationEmailModel_;

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
