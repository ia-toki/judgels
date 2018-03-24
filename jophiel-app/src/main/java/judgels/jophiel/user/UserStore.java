package judgels.jophiel.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import judgels.jophiel.user.password.PasswordHash;
import judgels.persistence.api.Page;

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

    public Optional<User> findUserByJid(String userJid) {
        return userDao.selectByJid(userJid).map(this::fromModel);
    }

    public Optional<String> findUserEmailByJid(String userJid) {
        return userDao.selectByJid(userJid).map(model -> model.email);
    }

    public Map<String, User> findUsersByJids(Set<String> userJids) {
        Map<String, UserModel> userModels = userDao.selectByJids(userJids);
        return userModels.values().stream().map(this::fromModel).collect(Collectors.toMap(User::getJid, p -> p));
    }

    public Optional<User> findUserByUsername(String username) {
        return userDao.selectByUsername(username).map(this::fromModel);
    }

    public Optional<User> findUserByUsernameAndPassword(String username, String password) {
        return userDao.selectByUsername(username)
                .filter(model -> validatePassword(password, model.password))
                .map(this::fromModel);
    }

    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        return userDao.selectByEmail(email)
                .filter(model -> validatePassword(password, model.password))
                .map(this::fromModel);
    }

    public Optional<User> findUserByEmail(String email) {
        return userDao.selectByEmail(email).map(this::fromModel);
    }

    public List<User> getUsersByTerm(String term) {
        return Lists.transform(userDao.selectAllByTerm(term), this::fromModel);
    }

    public Page<User> getUsers(int page, int pageSize) {
        Page<UserModel> models = userDao.selectAll(page, pageSize);
        return models.mapData(data -> Lists.transform(data, this::fromModel));
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

    public Optional<User> updateUserAvatar(String userJid, @Nullable String newAvatarFilename) {
        return userDao.selectByJid(userJid).map(model -> {
            model.avatarFilename = newAvatarFilename;
            return fromModel(userDao.update(model));
        });
    }

    private User fromModel(UserModel model) {
        Optional<String> avatarUrl = Optional.ofNullable(model.avatarFilename)
                .map(ImmutableList::of)
                .map(userAvatarFs::getPublicFileUrl);

        return new User.Builder()
                .jid(model.jid)
                .username(model.username)
                .avatarUrl(avatarUrl)
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

    public Map<String, User> findUsersByUsernames(Set<String> usernames) {
        Map<String, UserModel> userModelByUsernames = userDao.selectAllByUsernames(usernames);
        return userModelByUsernames.values().stream()
                .map(this::fromModel)
                .collect(Collectors.toMap(User::getUsername, p -> p));
    }

}
