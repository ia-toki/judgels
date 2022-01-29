package judgels.jophiel.user;

import com.google.common.collect.Lists;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.user.avatar.UserAvatarFs;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public class UserStore {
    private static final int PAGE_SIZE = 250;

    private final UserDao userDao;
    private final FileSystem userAvatarFs;

    @Inject
    public UserStore(UserDao userDao, @UserAvatarFs FileSystem userAvatarFs) {
        this.userDao = userDao;
        this.userAvatarFs = userAvatarFs;
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

    public Optional<User> getUserByJid(String userJid) {
        return userDao.selectByJid(userJid).map(UserStore::fromModel);
    }

    public Optional<String> getUserEmailByJid(String userJid) {
        return userDao.selectByJid(userJid).map(model -> model.email);
    }

    public Map<String, User> getUsersByJids(Set<String> userJids) {
        Map<String, UserModel> userModels = userDao.selectByJids(userJids);
        return userModels.values().stream().map(UserStore::fromModel).collect(Collectors.toMap(User::getJid, p -> p));
    }

    public Optional<User> getUserByUsername(String username) {
        return userDao.selectByUsername(username).map(UserStore::fromModel);
    }

    public Map<String, User> getUsersByUsername(Set<String> usernames) {
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

    public List<User> getUsersByTerm(String term) {
        return Lists.transform(userDao.selectAllByTerm(term), UserStore::fromModel);
    }

    public Page<User> getUsers(Optional<Integer> page, Optional<String> orderBy, Optional<OrderDir> orderDir) {
        SelectionOptions.Builder options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .pageSize(PAGE_SIZE)
                .orderBy("username")
                .orderDir(OrderDir.ASC);
        page.ifPresent(options::page);
        orderBy.ifPresent(options::orderBy);
        orderDir.ifPresent(options::orderDir);

        Page<UserModel> models = userDao.selectPaged(options.build());
        return models.mapPage(p -> Lists.transform(p, UserStore::fromModel));
    }

    public Optional<User> updateUser(String userJid, UserUpdateData data) {
        return userDao.selectByJid(userJid).map(model -> {
            toModel(data, model);
            return fromModel(userDao.updateByJid(userJid, model));
        });
    }

    public void validateUserPassword(String userJid, String password) {
        userDao.selectByJid(userJid).ifPresent(model -> {
            if (!validatePassword(password, model.password)) {
                throw new IllegalArgumentException();
            }
        });
    }

    public void updateUserPassword(String userJid, String newPassword) {
        userDao.selectByJid(userJid).ifPresent(model -> {
            updateUser(userJid, new UserUpdateData.Builder()
                    .username(model.username)
                    .password(newPassword)
                    .build());
        });
    }

    public Optional<String> getUserAvatarUrl(String userJid) {
        return userDao.selectByJid(userJid).flatMap(model ->
                Optional.ofNullable(model.avatarFilename).map(Paths::get).map(userAvatarFs::getPublicFileUrl));
    }

    public Optional<User> updateUserAvatar(String userJid, @Nullable String newAvatarFilename) {
        return userDao.selectByJid(userJid).map(model -> {
            model.avatarFilename = newAvatarFilename;
            return fromModel(userDao.update(model));
        });
    }

    public Map<String, String> translateUsernamesToJids(Set<String> usernames) {
        return userDao.selectAllByUsernames(usernames).stream()
                .map(UserStore::fromModel)
                .collect(Collectors.toMap(User::getUsername, User::getJid));
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
        model.email = data.getEmail();
        setPassword(model, data.getPassword());
    }

    private static void toModel(UserUpdateData data, UserModel model) {
        model.username = data.getUsername();
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
