package judgels.jophiel.user;

import com.google.common.collect.Lists;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;

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

    public User createUserWithJid(String jid, UserData userData) {
        UserModel model = new UserModel();
        toModel(userData, model);
        return fromModel(userDao.insertWithJid(jid, model));
    }

    public Optional<User> getUserById(int userId) {
        return userDao.selectById(userId).map(UserStore::fromModel);
    }

    public Optional<User> getUserByJid(String userJid) {
        return userDao.selectByJid(userJid).map(UserStore::fromModel);
    }

    public Optional<String> getUserEmailByJid(String userJid) {
        return userDao.selectByJid(userJid).map(model -> model.email);
    }

    public Map<String, User> getUsersByJids(Collection<String> userJids) {
        Map<String, UserModel> userModels = userDao.selectByJids(userJids);
        return userModels.values().stream().map(UserStore::fromModel).collect(Collectors.toMap(User::getJid, p -> p));
    }

    public Optional<User> getUserByUsername(String username) {
        return userDao.selectByUsername(username).map(UserStore::fromModel);
    }

    public Map<String, User> getUsersByUsername(Collection<String> usernames) {
        return userDao.selectAllByUsernames(usernames).stream()
                .collect(Collectors.toMap(m -> m.username, UserStore::fromModel));
    }

    public Optional<User> getUserByUsernameAndPassword(String username, String password) {
        return userDao.selectByUsername(username)
                .filter(model -> validatePassword(password, model.password))
                .map(UserStore::fromModel);
    }

    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        return userDao.selectByEmail(email)
                .filter(model -> validatePassword(password, model.password))
                .map(UserStore::fromModel);
    }

    public Optional<User> getUserByEmail(String email) {
        return userDao.selectByEmail(email).map(UserStore::fromModel);
    }

    public Page<User> getUsers(int pageNumber, int pageSize, Optional<String> orderBy, Optional<OrderDir> orderDir) {
        return userDao.select()
                .orderBy(orderBy.orElse(UserModel_.USERNAME), orderDir.orElse(OrderDir.ASC))
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, UserStore::fromModel));
    }

    public User updateUser(String userJid, UserUpdateData data) {
        UserModel model = userDao.findByJid(userJid);
        toModel(data, model);
        return fromModel(userDao.updateByJid(userJid, model));
    }

    public void updateUserPassword(String userJid, String newPassword) {
        updateUser(userJid, new UserUpdateData.Builder()
                .password(newPassword)
                .build());
    }

    public Optional<String> getUserAvatarFilename(String userJid) {
        return userDao.selectByJid(userJid).flatMap(model ->
                Optional.ofNullable(model.avatarFilename));
    }

    public void updateUserAvatar(String userJid, @Nullable String newAvatarFilename) {
        UserModel model = userDao.findByJid(userJid);
        model.avatarFilename = newAvatarFilename;
        userDao.update(model);
    }

    public Optional<String> translateUsernameToJid(String username) {
        return userDao.selectByUsername(username).map(m -> m.jid);
    }

    public Map<String, String> translateUsernamesToJids(Collection<String> usernames) {
        return userDao.selectAllByUsernames(usernames).stream()
                .collect(Collectors.toMap(m -> m.username, m -> m.jid));
    }

    private static User fromModel(UserModel model) {
        return new User.Builder()
                .id((int) model.id)
                .jid(model.jid)
                .username(model.username)
                .email(model.email)
                .build();
    }

    private static void toModel(UserData data, UserModel model) {
        model.username = data.getUsername();
        model.email = data.getEmail();
        setPassword(model, data.getPassword());
    }

    private static void toModel(UserUpdateData data, UserModel model) {
        data.getUsername().ifPresent(username -> model.username = username);
        data.getEmail().ifPresent(email -> model.email = email);
        data.getPassword().ifPresent(password -> setPassword(model, data.getPassword()));
    }

    private static void setPassword(UserModel model, Optional<String> password) {
        if (password.isPresent()) {
            model.password = hashPassword(password.get());
        } else {
            model.password = "";
        }
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
