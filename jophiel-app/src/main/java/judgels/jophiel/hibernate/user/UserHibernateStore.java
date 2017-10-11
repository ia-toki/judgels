package judgels.jophiel.hibernate.user;

import io.dropwizard.hibernate.AbstractDAO;
import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.user.UserStore;
import org.hibernate.SessionFactory;

public class UserHibernateStore extends AbstractDAO<UserModel> implements UserStore {
    public UserHibernateStore(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(get(userId)).map(UserHibernateStore::userFromModel);
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
