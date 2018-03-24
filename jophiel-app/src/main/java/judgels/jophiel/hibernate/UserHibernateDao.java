package judgels.jophiel.hibernate;

import com.google.common.collect.ImmutableMap;
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
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserModel_;
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
    public List<UserModel> selectAllByTerm(String term) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserModel> cq = cb.createQuery(getEntityClass());
        Root<UserModel> root = cq.from(getEntityClass());
        cq.where(cb.like(root.get(UserModel_.username), "%" + term + "%"));
        return currentSession().createQuery(cq).getResultList();
    }

    @Override
    public Map<String, UserModel> selectAllByUsernames(Set<String> usernames) {
        return selectAllByColumnIn(ImmutableMap.of(), UserModel_.username, usernames)
                .stream()
                .collect(Collectors.toMap(m -> m.jid, m -> m));
    }
}
