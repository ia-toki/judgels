package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import judgels.jophiel.user.UserDao;
import judgels.jophiel.user.UserModel;
import judgels.jophiel.user.UserModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

public class UserHibernateDao extends JudgelsHibernateDao<UserModel> implements UserDao {
    public UserHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<UserModel> selectByUsername(String username) {
        return selectByUniqueColumn(UserModel_.username, username);
    }

    @Override
    public Optional<UserModel> selectByEmail(String email) {
        return selectByUniqueColumn(UserModel_.email, email);
    }
}
