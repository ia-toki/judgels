package judgels.jophiel.session;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.session.SessionErrors;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.api.user.User;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.account.UserRegistrationEmailStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class SessionResource implements SessionService {
    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private UserStore userStore;
    private UserRegistrationEmailStore userRegistrationEmailStore;
    private SessionStore sessionStore;
    private SessionConfiguration sessionConfiguration;

    @Inject
    public SessionResource(
            ActorChecker actorChecker,
            UserRoleChecker roleChecker,
            UserStore userStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            SessionStore sessionStore,
            SessionConfiguration sessionConfiguration) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
        this.sessionStore = sessionStore;
        this.sessionConfiguration = sessionConfiguration;
    }

    @Override
    @UnitOfWork
    public Session logIn(Credentials credentials) {
        User user = userStore.getUserByUsernameAndPassword(credentials.getUsernameOrEmail(), credentials.getPassword())
                .orElseGet(() ->
                    userStore.getUserByEmailAndPassword(credentials.getUsernameOrEmail(), credentials.getPassword())
                    .orElseThrow(ForbiddenException::new));

        if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
            throw SessionErrors.userNotActivated(user.getEmail());
        }

        if (!roleChecker.canAdminister(user.getJid())) {
            int maxConcurrentSessionsPerUser = sessionConfiguration.getMaxConcurrentSessionsPerUser().orElse(-1);
            if (maxConcurrentSessionsPerUser >= 0) {
                if (sessionStore.getSessionsByUserJid(user.getJid()).size() >= maxConcurrentSessionsPerUser) {
                    throw SessionErrors.userMaxConcurrentSessionsExceeded(
                            user.getUsername(), maxConcurrentSessionsPerUser);
                }
            }
        }

        return sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
    }

    @Override
    @UnitOfWork
    public void logOut(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        if (!roleChecker.canAdminister(actorJid) && sessionConfiguration.getDisableLogOut().orElse(false)) {
            throw SessionErrors.logOutDisabled();
        }

        sessionStore.deleteSessionByToken(authHeader.getBearerToken());
        actorChecker.clear();
    }
}
