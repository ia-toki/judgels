package org.iatoki.judgels.sandalphon.user;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.api.jophiel.JophielUser;
import org.iatoki.judgels.jophiel.user.UserTokens;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.SandalphonUtils;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Singleton
public final class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Inject
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void upsertUser(String userJid, String accessToken, String idToken, String refreshToken, long expireTime) {
        if (userDao.existsByJid(userJid)) {
            UserModel userModel = userDao.findByJid(userJid);

            userModel.accessToken = accessToken;
            userModel.refreshToken = refreshToken;
            userModel.idToken = idToken;
            userModel.expirationTime = expireTime;

            userDao.edit(userModel, "guest", IdentityUtils.getIpAddress());
        } else {
            UserModel userModel = new UserModel();
            userModel.userJid = userJid;
            userModel.roles = StringUtils.join(SandalphonUtils.getDefaultRoles(), ",");

            userModel.accessToken = accessToken;
            userModel.refreshToken = refreshToken;
            userModel.idToken = idToken;
            userModel.expirationTime = expireTime;

            userDao.persist(userModel, "guest", IdentityUtils.getIpAddress());
        }
    }

    @Override
    public boolean existsByUserJid(String userJid) {
        return userDao.existsByJid(userJid);
    }

    @Override
    public User findUserById(long userId) throws UserNotFoundException {
        UserModel userModel = userDao.findById(userId);
        if (userModel == null) {
            throw new UserNotFoundException("User not found.");
        }

        return createUserFromUserModel(userModel);
    }

    @Override
    public User findUserByJid(String userJid) {
        UserModel userModel = userDao.findByJid(userJid);

        return createUserFromUserModel(userModel);
    }

    @Override
    public void createUser(String userJid, List<String> roles, String createUserJid, String createUserIpAddress) {
        UserModel userModel = new UserModel();
        userModel.userJid = userJid;
        userModel.roles = StringUtils.join(roles, ",");

        userDao.persist(userModel, createUserJid, createUserIpAddress);
    }

    @Override
    public void updateUser(String userJid, List<String> roles, String updateUserJid, String updateUserIpAddress) {
        UserModel userModel = userDao.findByJid(userJid);
        userModel.roles = StringUtils.join(roles, ",");

        userDao.edit(userModel, updateUserJid, updateUserIpAddress);
    }

    @Override
    public void deleteUser(String userJid) {
        UserModel userModel = userDao.findByJid(userJid);
        userDao.remove(userModel);
    }

    @Override
    public Page<User> getPageOfUsers(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = userDao.countByFilters(filterString);
        List<UserModel> userModels = userDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);
        List<User> users = Lists.transform(userModels, m -> createUserFromUserModel(m));
        return new Page<>(users, totalPages, pageIndex, pageSize);
    }

    @Override
    public void upsertUserFromJophielUser(JophielUser jophielUser, String upsertUserJid, String upsertUserIpAddress) {
        upsertUserFromJophielUser(jophielUser, SandalphonUtils.getDefaultRoles(), upsertUserJid, upsertUserIpAddress);
    }

    @Override
    public void upsertUserFromJophielUser(JophielUser jophielUser, List<String> roles, String upsertUserJid, String upsertUserIpAddress) {
        if (!userDao.existsByJid(jophielUser.getJid())) {
            createUser(jophielUser.getJid(), roles, upsertUserJid, upsertUserIpAddress);
        }

        JidCacheServiceImpl.getInstance().putDisplayName(jophielUser.getJid(), JudgelsPlayUtils.getUserDisplayName(jophielUser.getUsername()), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
    }

    @Override
    public UserTokens getUserTokensByUserJid(String userJid) {
        UserModel userModel = userDao.findByJid(userJid);

        return createUserTokensFromUserModel(userModel);
    }

    private static UserTokens createUserTokensFromUserModel(UserModel userModel) {
        return new UserTokens(userModel.userJid, userModel.accessToken, userModel.refreshToken, userModel.idToken, userModel.expirationTime);
    }

    private static User createUserFromUserModel(UserModel userModel) {
        return new User(userModel.id, userModel.userJid, Arrays.asList(userModel.roles.split(",")));
    }
}
