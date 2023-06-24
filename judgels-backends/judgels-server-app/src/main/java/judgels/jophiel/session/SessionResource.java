package judgels.jophiel.session;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.GoogleCredentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.session.SessionErrors;
import judgels.jophiel.api.user.User;
import judgels.jophiel.auth.google.GoogleAuth;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.account.UserRegistrationEmailStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/session")
public class SessionResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected UserRegistrationEmailStore userRegistrationEmailStore;
    @Inject protected SessionStore sessionStore;
    @Inject protected SessionConfiguration sessionConfiguration;
    @Inject protected Optional<GoogleAuth> googleAuth;

    @Inject public SessionResource() {}

    @POST
    @Path("/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
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
            int maxConcurrentSessionsPerUser = sessionConfiguration.getMaxConcurrentSessionsPerUser();
            if (maxConcurrentSessionsPerUser >= 0) {
                if (sessionStore.getSessionsByUserJid(user.getJid()).size() >= maxConcurrentSessionsPerUser) {
                    throw SessionErrors.userMaxConcurrentSessionsExceeded();
                }
            }
        }

        return sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
    }

    @POST
    @Path("/login-google")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Session logInWithGoogle(GoogleCredentials credentials) {
        String email = checkFound(googleAuth).verifyIdToken(credentials.getIdToken()).getEmail();

        User user = userStore.getUserByEmail(email).orElseThrow(ForbiddenException::new);
        return sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
    }

    @POST
    @Path("/logout")
    @UnitOfWork
    public void logOut(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        if (!roleChecker.canAdminister(actorJid) && sessionConfiguration.getDisableLogout()) {
            throw SessionErrors.logoutDisabled();
        }

        sessionStore.deleteSessionByToken(authHeader.getBearerToken());
        actorChecker.clear();
    }
}
