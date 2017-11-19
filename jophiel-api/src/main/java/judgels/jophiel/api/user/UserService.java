package judgels.jophiel.api.user;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users")
public interface UserService {
    @GET
    @Path("/{userJid}")
    @Produces(APPLICATION_JSON)
    User getUser(@PathParam("userJid") String userJid);

    @GET
    @Path("/username/{username}")
    @Produces(APPLICATION_JSON)
    User getUserByUsername(@PathParam("username") String username);

    @GET
    @Path("/me")
    @Produces(APPLICATION_JSON)
    User getMyself(@HeaderParam(AUTHORIZATION) AuthHeader authHeader);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    User createUser(User.Data userData);

    @PUT
    @Path("/{userJid}")
    @Consumes(APPLICATION_JSON)
    void updateUser(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid,
            User.Data userData);
}
