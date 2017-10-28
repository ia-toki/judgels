package judgels.jophiel.user;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;

public class UserStore {
    private final UserDao userDao;

    @Inject
    public UserStore(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> findUserById(long userId) {
        return userDao.select(userId).map(UserStore::fromModel);
    }

    public Optional<User> findUserByJid(String userJid) {
        return userDao.selectByJid(userJid).map(UserStore::fromModel);
    }

    public User createUser(User.Data userData) {
        UserModel model = new UserModel();
        toModel(userData, model);
        return fromModel(userDao.insert(model));
    }

    public Optional<User> updateUser(String userJid, User.Data userData) {
        return userDao.selectByJid(userJid).map(model -> {
            toModel(userData, model);
            return fromModel(userDao.updateByJid(userJid, model));
        });
    }

    private static User fromModel(UserModel model) {
        return new User.Builder()
                .id(model.id)
                .jid(model.jid)
                .username(model.username)
                .name(model.name)
                .email(model.email)
                .build();
    }

    private static void toModel(User.Data data, UserModel model) {
        model.username = data.getUsername();
        model.name = data.getName();
        model.email = data.getEmail();
    }
}
