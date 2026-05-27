package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.UserRoleDao;
import judgels.persistence.model.UserRoleModel;
import judgels.persistence.model.UserRoleModel_;

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
