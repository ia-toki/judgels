package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jophiel.user.UserDao;
import judgels.jophiel.user.UserModel;
import judgels.jophiel.user.UserModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class UserHibernateDao extends JudgelsHibernateDao<UserModel> implements UserDao {
    @Inject
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

    @Override
    public List<UserModel> selectByTerm(String term) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserModel> cq = cb.createQuery(getEntityClass());
        Root<UserModel> root = cq.from(getEntityClass());
        cq.where(cb.like(root.get(UserModel_.username), "%" + term + "%"));
        return currentSession().createQuery(cq).getResultList();
    }
}
