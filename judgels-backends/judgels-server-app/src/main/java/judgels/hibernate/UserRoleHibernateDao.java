package judgels.hibernate;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.UserRoleDao;
import judgels.persistence.UserRoleModel;
import judgels.persistence.UserRoleModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class UserRoleHibernateDao extends HibernateDao<UserRoleModel> implements UserRoleDao {
    @Inject
    public UserRoleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<UserRoleModel> selectByUserJid(String userJid) {
        return select().where(columnEq(UserRoleModel_.userJid, userJid)).unique();
    }
}
