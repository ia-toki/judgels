package judgels.jophiel.hibernate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

@Singleton
public class UserHibernateDao extends JudgelsHibernateDao<UserModel> implements UserDao {
    @Inject
    public UserHibernateDao(HibernateDaoData data) {
        super(data);
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
        return selectAll(new FilterOptions.Builder<UserModel>()
                .addCustomPredicates((cb, cq, root) -> cb.like(root.get(UserModel_.username), "%" + term + "%"))
                .build());
    }

    @Override
    public List<UserModel> selectAllByUsernames(Set<String> usernames) {
        return selectAll(new FilterOptions.Builder<UserModel>()
                .putColumnsIn(UserModel_.username, usernames)
                .build());
    }
}
