package judgels.contrib.jophiel.session;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import judgels.contrib.jophiel.auth.google.GoogleAuth;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;
import tlx.jophiel.api.session.GoogleCredentials;

@Path("/api/v2/session")
public class ContribSessionResource {
    @Inject protected UserStore userStore;
    @Inject protected SessionStore sessionStore;
    @Inject protected Optional<GoogleAuth> googleAuth;

    @Inject public ContribSessionResource() {}

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
}
