package judgels.session;

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
import java.util.Optional;
import judgels.api.session.Credentials;
import judgels.api.session.Session;
import judgels.api.session.SessionErrors;
import judgels.api.user.User;
import judgels.contrib.api.session.SessionWithRegistrationErrors;
import judgels.contrib.user.registration.UserRegistrationConfiguration;
import judgels.contrib.user.registration.UserRegistrationEmailStore;
import judgels.persistence.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.user.UserRoleChecker;
import judgels.user.UserStore;

@Path("/api/v2/session")
public class SessionResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected Optional<UserRegistrationConfiguration> userRegistrationConfig;
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

        if (userRegistrationConfig.isPresent()) {
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
