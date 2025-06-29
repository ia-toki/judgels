package judgels.jophiel.session;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import judgels.app.JudgelsApp;
import judgels.contrib.jophiel.api.session.SessionWithRegistrationErrors;
import judgels.contrib.jophiel.user.registration.UserRegistrationEmailStore;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.session.SessionErrors;
import judgels.jophiel.api.user.User;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
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

        if (JudgelsApp.isTLX()) {
            if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
                throw SessionWithRegistrationErrors.userNotActivated(user.getEmail());
            }
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
