package judgels.jophiel.user;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import judgels.jophiel.api.user.PasswordResetData;
import judgels.jophiel.api.user.PasswordUpdateData;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserProfile;
import judgels.jophiel.api.user.UserRegistrationData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.user.password.UserPasswordResetter;
import judgels.jophiel.user.profile.UserProfileStore;
import judgels.jophiel.user.registration.UserRegisterer;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {

    private final ActorChecker actorChecker;
    private final UserStore userStore;
    private final UserProfileStore userProfileStore;
    private final Optional<UserRegisterer> userRegisterer;
    private final UserPasswordResetter userPasswordResetter;

    @Inject
    public UserResource(
            ActorChecker actorChecker,
            UserStore userStore,
            UserProfileStore userProfileStore,
            Optional<UserRegisterer> userRegisterer,
            UserPasswordResetter userPasswordResetter) {

        this.actorChecker = actorChecker;
        this.userStore = userStore;
        this.userProfileStore = userProfileStore;
        this.userRegisterer = userRegisterer;
        this.userPasswordResetter = userPasswordResetter;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUser(String userJid) {
        return userStore.findUserByJid(userJid).orElseThrow(NotFoundException::new);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUserByUsername(String username) {
        return userStore.findUserByUsername(username).orElseThrow(NotFoundException::new);
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
        return getUser(actorJid);
    }

    @Override
    @UnitOfWork
    public void updateMyPassword(AuthHeader authHeader, PasswordUpdateData passwordUpdateData) {
        String actorJid = actorChecker.check(authHeader);
        if (!userStore.validateUserPassword(actorJid, passwordUpdateData.getOldPassword())) {
            throw new IllegalArgumentException();
        }
        userStore.updateUserPassword(actorJid, passwordUpdateData.getNewPassword());
    }

    @Override
    @UnitOfWork
    public User createUser(AuthHeader authHeader, UserData userData) {
        actorChecker.check(authHeader);
        return userStore.createUser(userData);
    }

    @Override
    @UnitOfWork
    public User registerUser(UserRegistrationData userRegistrationData) {
        return userRegisterer.orElseThrow(NotFoundException::new).register(userRegistrationData);
    }

    @Override
    @UnitOfWork
    public void activateUser(String emailCode) {
        userRegisterer.orElseThrow(NotFoundException::new).activate(emailCode);
    }

    @Override
    @UnitOfWork
    public void updateUser(AuthHeader authHeader, String userJid, UserData userData) {
        actorChecker.check(authHeader);
        userStore.updateUser(userJid, userData);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserProfile getUserProfile(AuthHeader authHeader, String userJid) {
        actorChecker.check(authHeader);
        return userProfileStore.getUserProfile(userJid);
    }

    @Override
    @UnitOfWork
    public void updateUserProfile(AuthHeader authHeader, String userJid, UserProfile userProfile) {
        actorChecker.check(authHeader);
        if (!userStore.findUserByJid(userJid).isPresent()) {
            throw new NotFoundException();
        }
        userProfileStore.upsertUserProfile(userJid, userProfile);
    }

    @Override
    @UnitOfWork
    public void requestToResetUserPassword(String email) {
        userPasswordResetter.request(email);
    }

    @Override
    @UnitOfWork
    public void resetUserPassword(PasswordResetData passwordResetData) {
        userPasswordResetter.reset(passwordResetData);
    }
}
