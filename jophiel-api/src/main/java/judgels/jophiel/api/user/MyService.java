package judgels.jophiel.api.user;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jophiel.api.role.Role;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users/me")
public interface MyService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    UserWithAvatar getMyself(@HeaderParam(AUTHORIZATION) AuthHeader authHeader);

    @POST
    @Path("/password")
    @Consumes(APPLICATION_JSON)
    void updateMyPassword(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, PasswordUpdateData passwordUpdateData);

    @GET
    @Path("/role")
    @Produces(APPLICATION_JSON)
    Role getMyRole(@HeaderParam(AUTHORIZATION) AuthHeader authHeader);
}
