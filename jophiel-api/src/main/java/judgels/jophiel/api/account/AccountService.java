package judgels.jophiel.api.account;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jophiel.api.session.Session;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/account")
public interface AccountService {
    @POST
    @Path("/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Session logIn(Credentials credentials);

    @POST
    @Path("/logout")
    void logOut(@HeaderParam(AUTHORIZATION) AuthHeader authHeader);
}
