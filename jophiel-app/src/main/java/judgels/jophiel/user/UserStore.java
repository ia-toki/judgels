package judgels.jophiel.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;

public class UserStore {
    private final UserDao userDao;

    @Inject
    public UserStore(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(UserData userData) {
        UserModel model = new UserModel();
        toModel(userData, model);
        return fromModel(userDao.insert(model));
    }

    public Optional<User> findUserByJid(String userJid) {
        return userDao.selectByJid(userJid).map(UserStore::fromModel);
    }

    public Optional<User> findUserByUsername(String username) {
        return userDao.selectByUsername(username).map(UserStore::fromModel);
    }

    public Optional<User> findUserByUsernameAndPassword(String username, String password) {
        return userDao.selectByUsername(username)
                .filter(model -> validatePassword(password, model.password))
                .map(UserStore::fromModel);
    }

    public Optional<User> findUserByEmail(String email) {
        return userDao.selectByEmail(email).map(UserStore::fromModel);
    }

    public Optional<User> updateUser(String userJid, UserData userData) {
        return userDao.selectByJid(userJid).map(model -> {
            toModel(userData, model);
            return fromModel(userDao.updateByJid(userJid, model));
        });
    }

    private static User fromModel(UserModel model) {
        return new User.Builder()
                .jid(model.jid)
                .username(model.username)
                .email(model.email)
                .build();
    }

    private static void toModel(UserData data, UserModel model) {
        model.username = data.getUsername();
        model.password = hashPassword(data.getPassword());
        model.email = data.getEmail();
    }

    private static String hashPassword(String password) {
        try {
            return PasswordHash.createHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean validatePassword(String password, String correctPassword) {
        try {
            return PasswordHash.validatePassword(password, correctPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }
}
