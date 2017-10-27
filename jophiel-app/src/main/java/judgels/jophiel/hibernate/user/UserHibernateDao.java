package judgels.jophiel.hibernate.user;

import java.time.Clock;
import javax.inject.Inject;
import judgels.hibernate.model.JudgelsHibernateDao;
import judgels.jophiel.user.UserDao;
import judgels.model.ActorProvider;
import org.hibernate.SessionFactory;

public class UserHibernateDao extends JudgelsHibernateDao<UserModel> implements UserDao {
    @Inject
    public UserHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
