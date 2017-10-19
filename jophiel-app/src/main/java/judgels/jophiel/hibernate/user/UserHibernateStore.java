package judgels.jophiel.hibernate.user;

import io.dropwizard.hibernate.AbstractDAO;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jophiel.api.user.User;
import judgels.jophiel.user.UserStore;
import org.hibernate.SessionFactory;

public class UserHibernateStore extends AbstractDAO<UserModel> implements UserStore {
    public UserHibernateStore(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Optional<User> findByJid(String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserModel> cq = cb.createQuery(getEntityClass());
        Root<UserModel> root = cq.from(getEntityClass());
        cq.where(cb.equal(root.get(UserModel_.jid), userJid));
        return Optional.ofNullable(uniqueResult(cq)).map(UserHibernateStore::userFromModel);
    }

    @Override
    public void insert(User user) {
        persist(userToModel(user));
    }

    private static User userFromModel(UserModel model) {
        return new User.Builder()
                .jid(model.jid)
                .username(model.username)
                .name(model.name)
                .email(model.email)
                .build();
    }

    private static UserModel userToModel(User user) {
        UserModel model = new UserModel();
        model.jid = user.getJid();
        model.username = user.getUsername();
        model.name = user.getName();
        model.email = user.getEmail();
        return model;
    }
}
