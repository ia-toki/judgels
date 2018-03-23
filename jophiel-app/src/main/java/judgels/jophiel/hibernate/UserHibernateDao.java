package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserModel_;
import judgels.jophiel.user.UserDao;
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

    @Override
    public Map<String, UserModel> selectByUsernames(Set<String> usernames) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserModel> cq = cb.createQuery(getEntityClass());
        Root<UserModel> root = cq.from(getEntityClass());

        cq.where(root.get(UserModel_.username).in(usernames));

        List<UserModel> result = currentSession().createQuery(cq).getResultList();
        return result.stream().collect(Collectors.toMap(p -> p.username, p -> p));
    }

}
