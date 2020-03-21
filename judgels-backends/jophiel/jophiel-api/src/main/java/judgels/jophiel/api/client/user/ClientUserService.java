package judgels.jophiel.api.client.user;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.jophiel.api.role.UserRole;

// TODO(fushar): Add client authorization to Jophiel if needed

@Path("/api/v2/client/users")
public interface ClientUserService {
    @GET
    @Path("/{userJid}/role")
    @Produces(APPLICATION_JSON)
    UserRole getUserRole(@PathParam("userJid") String userJid);
}
