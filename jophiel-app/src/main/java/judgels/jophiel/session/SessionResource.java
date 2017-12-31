package judgels.jophiel.session;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.api.user.User;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.registration.UserRegistrationEmailStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class SessionResource implements SessionService {
    private final ActorChecker actorChecker;
    private UserStore userStore;
    private UserRegistrationEmailStore userRegistrationEmailStore;
    private SessionStore sessionStore;

    @Inject
    public SessionResource(
            ActorChecker actorChecker,
            UserStore userStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            SessionStore sessionStore) {

        this.actorChecker = actorChecker;
        this.userStore = userStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
        this.sessionStore = sessionStore;
    }

    @Override
    @UnitOfWork
    public Session logIn(Credentials credentials) {
        User user = userStore.findUserByUsernameAndPassword(credentials.getUsername(), credentials.getPassword())
                .orElseThrow(ForbiddenException::new);

        if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
            throw new ForbiddenException();
        }

        return sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
    }

    @Override
    @UnitOfWork
    public void logOut(AuthHeader authHeader) {
        actorChecker.check(authHeader);
        sessionStore.deleteSessionByToken(authHeader.getBearerToken());
    }
}
