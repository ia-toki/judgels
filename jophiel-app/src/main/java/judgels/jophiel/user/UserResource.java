package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.PasswordResetData;
import judgels.jophiel.api.user.PasswordUpdateData;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserProfile;
import judgels.jophiel.api.user.UserRegistrationData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.role.RoleChecker;
import judgels.jophiel.user.password.UserPasswordResetter;
import judgels.jophiel.user.profile.UserProfileStore;
import judgels.jophiel.user.registration.UserRegisterer;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final UserStore userStore;
    private final UserProfileStore userProfileStore;
    private final Optional<UserRegisterer> userRegisterer;
    private final Optional<UserPasswordResetter> userPasswordResetter;

    @Inject
    public UserResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            UserStore userStore,
            UserProfileStore userProfileStore,
            Optional<UserRegisterer> userRegisterer,
            Optional<UserPasswordResetter> userPasswordResetter) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.userProfileStore = userProfileStore;
        this.userRegisterer = userRegisterer;
        this.userPasswordResetter = userPasswordResetter;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUser(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadUser(actorJid, userJid));

        return checkFound(userStore.findUserByJid(userJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean usernameExists(String username) {
        return userStore.findUserByUsername(username).isPresent();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean emailExists(String email) {
        return userStore.findUserByEmail(email).isPresent();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getMyself(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);

        return checkFound(userStore.findUserByJid(actorJid));
    }

    @Override
    @UnitOfWork
    public void updateMyPassword(AuthHeader authHeader, PasswordUpdateData passwordUpdateData) {
        String actorJid = actorChecker.check(authHeader);

        userStore.validateUserPassword(actorJid, passwordUpdateData.getOldPassword());
        userStore.updateUserPassword(actorJid, passwordUpdateData.getNewPassword());
    }

    @Override
    @UnitOfWork
    public User createUser(AuthHeader authHeader, UserData userData) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canCreateUser(actorJid));

        return userStore.createUser(userData);
    }

    @Override
    @UnitOfWork
    public User registerUser(UserRegistrationData userRegistrationData) {
        return checkFound(userRegisterer).register(userRegistrationData);
    }

    @Override
    @UnitOfWork
    public void activateUser(String emailCode) {
        checkFound(userRegisterer).activate(emailCode);
    }

    @Override
    @UnitOfWork
    public User updateUser(AuthHeader authHeader, String userJid, UserData userData) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canMutateUser(actorJid, userJid));

        return checkFound(userStore.updateUser(userJid, userData));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserProfile getUserProfile(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadUser(actorJid, userJid));

        User user = checkFound(userStore.findUserByJid(userJid));
        return userProfileStore.getUserProfile(user.getJid());
    }

    @Override
    @UnitOfWork
    public UserProfile updateUserProfile(AuthHeader authHeader, String userJid, UserProfile userProfile) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canMutateUser(actorJid, userJid));

        User user = checkFound(userStore.findUserByJid(userJid));
        return userProfileStore.upsertUserProfile(user.getJid(), userProfile);
    }

    @Override
    @UnitOfWork
    public void deleteUserAvatar(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canMutateUser(actorJid, userJid));

        checkFound(userStore.updateUserAvatar(userJid, null));
    }

    @Override
    @UnitOfWork
    public void requestToResetUserPassword(String email) {
        User user = checkFound(userStore.findUserByEmail(email));
        checkFound(userPasswordResetter).request(user);
    }

    @Override
    @UnitOfWork
    public void resetUserPassword(PasswordResetData passwordResetData) {
        checkFound(userPasswordResetter).reset(passwordResetData);
    }

    @Override
    @UnitOfWork
    public Map<String, String> findUsernamesByJids(Set<String> jids) {
        Map<String, User> users = userStore.findUsersByJids(jids);
        return users.values().stream().collect(Collectors.toMap(User::getJid, User::getUsername));
    }
}
