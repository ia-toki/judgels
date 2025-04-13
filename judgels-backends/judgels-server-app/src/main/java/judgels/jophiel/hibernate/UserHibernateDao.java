package judgels.jophiel.hibernate;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class UserHibernateDao extends JudgelsHibernateDao<UserModel> implements UserDao {
    @Inject
    public UserHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<UserModel> selectByUsername(String username) {
        return select().where(columnEq(UserModel_.username, username)).unique();
    }

    @Override
    public Optional<UserModel> selectByEmail(String email) {
        return select().where(columnEq(UserModel_.email, email)).unique();
    }

    @Override
    public List<UserModel> selectAllByUsernames(Collection<String> usernames) {
        return select().where(columnIn(UserModel_.username, usernames)).all();
    }
}
