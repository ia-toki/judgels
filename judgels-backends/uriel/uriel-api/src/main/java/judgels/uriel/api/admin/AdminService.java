package judgels.uriel.api.admin;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/admins")
public interface AdminService {
    @PUT
    @Path("/{username}")
    void upsertAdmin(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, @PathParam("username") String username);
}
