package judgels.jophiel.account;

import com.palantir.remoting.api.errors.ErrorType;
import com.palantir.remoting.api.errors.ServiceException;
import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.account.AccountService;
import judgels.jophiel.api.account.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class AccountResource implements AccountService {
    private final ActorChecker actorChecker;
    private UserStore userStore;
    private SessionStore sessionStore;

    @Inject
    public AccountResource(ActorChecker actorChecker, UserStore userStore, SessionStore sessionStore) {
        this.actorChecker = actorChecker;
        this.userStore = userStore;
        this.sessionStore = sessionStore;
    }

    @Override
    @UnitOfWork
    public Session logIn(Credentials credentials) {
        User user = userStore.findUserByUsernameAndPassword(credentials.getUsername(), credentials.getPassword())
                .orElseThrow(() -> new ServiceException(ErrorType.PERMISSION_DENIED));

        return sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
    }

    @Override
    @UnitOfWork
    public void logOut(AuthHeader authHeader) {
        actorChecker.check(authHeader);
        sessionStore.deleteSessionByToken(authHeader.getBearerToken());
    }
}
