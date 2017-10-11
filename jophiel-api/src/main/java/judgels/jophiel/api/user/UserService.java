package judgels.jophiel.api.user;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.client.api.auth.BasicAuthHeader;

@Path("/api/v2/users")
public interface UserService {
    @GET
    @Path("/{userId}")
    @Produces(APPLICATION_JSON)
    User getUserById(@HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader, @PathParam("userId") long userId);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void createUser(@HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader, User user);
}
