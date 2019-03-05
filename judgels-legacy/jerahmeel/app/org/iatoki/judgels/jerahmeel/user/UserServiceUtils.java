package org.iatoki.judgels.jerahmeel.user;

import org.iatoki.judgels.jophiel.user.UserTokens;

import java.util.Arrays;

final class UserServiceUtils {

    private UserServiceUtils() {
        // prevent instantiation
    }

    static UserTokens createUserTokensFromUserModel(UserModel userModel) {
        return new UserTokens(userModel.userJid, userModel.accessToken, userModel.refreshToken, userModel.idToken, userModel.expirationTime);
    }

    static User createUserFromUserModel(UserModel userModel) {
        String roles = userModel.roles == null ? "" : userModel.roles;
        return new User(userModel.id, userModel.userJid, Arrays.asList(roles.split(",")));
    }
}
