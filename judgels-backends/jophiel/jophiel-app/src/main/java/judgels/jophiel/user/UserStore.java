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

    public List<User> createUsers(List<UserData> data) {
        List<UserModel> usersModel = Lists.transform(data, userData -> {
            UserModel model = new UserModel();
            toModel(userData, model);
            return model;
        });
        return fromModel(userDao.insertAll(usersModel));
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
                .orderBy("username").orderDir(OrderDir.ASC);
        page.ifPresent(options::page);
        orderBy.ifPresent(options::orderBy);
        orderDir.ifPresent(options::orderDir);

        Page<UserModel> models = userDao.selectPaged(options.build());
        return models.mapPage(p -> Lists.transform(p, UserStore::fromModel));
    }

    public Optional<User> updateUser(String userJid, UserData userData) {
        return userDao.selectByJid(userJid).map(model -> {
            toModel(userData, model);
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
            updateUser(userJid, new UserData.Builder()
                    .username(model.username)
                    .email(model.email)
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
        Map<String, UserModel> userModelByUsernames = userDao.selectAllByUsernames(usernames);
        return userModelByUsernames.values().stream()
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

    private static List<User> fromModel(List<UserModel> models) {
        return Lists.transform(models, model -> fromModel(model));
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
