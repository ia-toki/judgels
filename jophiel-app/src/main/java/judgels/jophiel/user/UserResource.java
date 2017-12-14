package judgels.jophiel.user;

import com.palantir.remoting.api.errors.ErrorType;
import com.palantir.remoting.api.errors.ServiceException;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.user.email.UserRegistrationEmailMailer;
import judgels.jophiel.user.email.UserRegistrationEmailStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {
    private final ActorChecker actorChecker;
    private final UserStore userStore;
    private final UserInfoStore userInfoStore;
    private final UserRegistrationEmailStore userRegistrationEmailStore;
    private final Optional<UserRegistrationEmailMailer> userVerificationEmailMailer;

    @Inject
    public UserResource(
            ActorChecker actorChecker,
            UserStore userStore,
            UserInfoStore userInfoStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            Optional<UserRegistrationEmailMailer> userVerificationEmailMailer) {

        this.actorChecker = actorChecker;
        this.userStore = userStore;
        this.userInfoStore = userInfoStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
        this.userVerificationEmailMailer = userVerificationEmailMailer;
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
    public User createUser(AuthHeader authHeader, UserData userData) {
        actorChecker.check(authHeader);
        return userStore.createUser(userData);
    }

    @Override
    @UnitOfWork
    public User registerUser(UserData userData) {
        User user = userStore.createUser(userData);
        userVerificationEmailMailer.ifPresent(mailer -> {
            String emailCode = userRegistrationEmailStore.generateEmailCode(user.getJid());
            mailer.sendVerificationEmail(user, emailCode);
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
    public UserInfo getUserInfo(AuthHeader authHeader, String userJid) {
        actorChecker.check(authHeader);
        return userInfoStore.getUserInfo(userJid);
    }

    @Override
    @UnitOfWork
    public void updateUserInfo(AuthHeader authHeader, String userJid, UserInfo userInfo) {
        actorChecker.check(authHeader);
        if (!userStore.findUserByJid(userJid).isPresent()) {
            throw new ServiceException(ErrorType.NOT_FOUND);
        }
        userInfoStore.upsertUserInfo(userJid, userInfo);
    }
}
