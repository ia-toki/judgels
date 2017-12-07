package judgels.jophiel.user;

import com.palantir.remoting.api.errors.ErrorType;
import com.palantir.remoting.api.errors.ServiceException;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.user.email.UserVerificationEmailMailer;
import judgels.jophiel.user.email.UserVerificationEmailStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {
    private final ActorChecker actorChecker;
    private final UserStore userStore;
    private final UserVerificationEmailStore userVerificationEmailStore;
    private final Optional<UserVerificationEmailMailer> userVerificationEmailMailer;

    @Inject
    public UserResource(
            ActorChecker actorChecker,
            UserStore userStore,
            UserVerificationEmailStore userVerificationEmailStore,
            Optional<UserVerificationEmailMailer> userVerificationEmailMailer) {

        this.actorChecker = actorChecker;
        this.userStore = userStore;
        this.userVerificationEmailStore = userVerificationEmailStore;
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
            String emailCode = userVerificationEmailStore.generateEmailCode(user.getJid());
            mailer.sendVerificationEmail(user, emailCode);
        });
        return user;
    }

    @Override
    @UnitOfWork
    public void verifyUserEmail(String emailCode) {
        if (!userVerificationEmailStore.verifyEmailCode(emailCode)) {
            throw new ServiceException(ErrorType.NOT_FOUND);
        }
    }

    @Override
    @UnitOfWork
    public void updateUser(AuthHeader authHeader, String userJid, UserData userData) {
        actorChecker.check(authHeader);
        userStore.updateUser(userJid, userData);
    }
}
