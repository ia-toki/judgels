package judgels.jophiel.api.session;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/session")
public interface SessionService {
    @POST
    @Path("/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Session logIn(Credentials credentials);

    @POST
    @Path("/login-google")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Session logInWithGoogle(GoogleCredentials credentials);

    @POST
    @Path("/logout")
    void logOut(@HeaderParam(AUTHORIZATION) AuthHeader authHeader);
}
