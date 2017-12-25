package judgels.jophiel.user;

import com.palantir.remoting.api.errors.ErrorType;
import com.palantir.remoting.api.errors.ServiceException;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.PasswordResetData;
import judgels.jophiel.api.user.PasswordUpdateData;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserProfile;
import judgels.jophiel.api.user.UserRegistrationData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.user.password.UserForgotPasswordMailer;
import judgels.jophiel.user.password.UserForgotPasswordStore;
import judgels.jophiel.user.profile.UserProfileStore;
import judgels.jophiel.user.registration.UserRegistrationEmailMailer;
import judgels.jophiel.user.registration.UserRegistrationEmailStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {
    private static final Duration FORGOT_PASSWORD_EXPIRATION = Duration.ofHours(1);

    private final ActorChecker actorChecker;
    private final UserStore userStore;
    private final UserProfileStore userProfileStore;
    private final UserRegistrationEmailStore userRegistrationEmailStore;
    private final Optional<UserRegistrationEmailMailer> userRegistrationEmailMailer;
    private final UserForgotPasswordStore userForgotPasswordStore;
    private final Optional<UserForgotPasswordMailer> userForgotPasswordMailer;

    @Inject
    public UserResource(
            ActorChecker actorChecker,
            UserStore userStore,
            UserProfileStore userProfileStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            Optional<UserRegistrationEmailMailer> userRegistrationEmailMailer,
            UserForgotPasswordStore userForgotPasswordStore,
            Optional<UserForgotPasswordMailer> userForgotPasswordMailer) {

        this.actorChecker = actorChecker;
        this.userStore = userStore;
        this.userProfileStore = userProfileStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
        this.userRegistrationEmailMailer = userRegistrationEmailMailer;
        this.userForgotPasswordStore = userForgotPasswordStore;
        this.userForgotPasswordMailer = userForgotPasswordMailer;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUser(String userJid) {
        return userStore.findUserByJid(userJid).orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUserByUsername(String username) {
        return userStore.findUserByUsername(username).orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND));
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
            throw new ServiceException(ErrorType.INVALID_ARGUMENT);
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
        UserData userData = new UserData.Builder()
                .username(userRegistrationData.getUsername())
                .password(userRegistrationData.getPassword())
                .email(userRegistrationData.getEmail())
                .build();
        User user = userStore.createUser(userData);

        UserProfile userProfile = new UserProfile.Builder()
                .name(userRegistrationData.getName())
                .build();
        userProfileStore.upsertUserProfile(user.getJid(), userProfile);

        userRegistrationEmailMailer.ifPresent(mailer -> {
            String emailCode = userRegistrationEmailStore.generateEmailCode(user.getJid());
            mailer.sendActivationEmail(user, emailCode);
        });
        return user;
    }

    @Override
    @UnitOfWork
    public void activateUser(String emailCode) {
        if (!userRegistrationEmailStore.verifyEmailCode(emailCode)) {
            throw new ServiceException(ErrorType.NOT_FOUND);
        }
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
            throw new ServiceException(ErrorType.NOT_FOUND);
        }
        userProfileStore.upsertUserProfile(userJid, userProfile);
    }

    @Override
    @UnitOfWork
    public void requestToResetUserPassword(String email) {
        Optional<User> maybeUser = userStore.findUserByEmail(email);
        if (!maybeUser.isPresent()) {
            throw new ServiceException(ErrorType.NOT_FOUND);
        }
        userForgotPasswordMailer.ifPresent(mailer -> {
            User user = maybeUser.get();
            String emailCode = userForgotPasswordStore.generateEmailCode(user.getJid(), FORGOT_PASSWORD_EXPIRATION);
            mailer.sendRequestEmail(user, emailCode);
        });
    }

    @Override
    @UnitOfWork
    public void resetUserPassword(PasswordResetData passwordResetData) {
        String emailCode = passwordResetData.getEmailCode();
        Optional<String> maybeUserJid = userForgotPasswordStore.consumeEmailCode(emailCode);
        if (!maybeUserJid.isPresent()) {
            throw new ServiceException(ErrorType.INVALID_ARGUMENT);
        }

        userStore.updateUserPassword(maybeUserJid.get(), passwordResetData.getNewPassword());
    }
}
