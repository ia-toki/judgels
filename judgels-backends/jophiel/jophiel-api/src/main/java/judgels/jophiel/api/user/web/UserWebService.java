package judgels.jophiel.api.user.web;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/user-web")
public interface UserWebService {
    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    UserWebConfig getWebConfig(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader);
}
