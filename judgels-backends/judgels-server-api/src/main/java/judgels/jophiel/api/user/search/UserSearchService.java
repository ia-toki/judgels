package judgels.jophiel.api.user.search;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/api/v2/user-search")
public interface UserSearchService {
    @GET
    @Path("/username-exists/{username}")
    @Produces(APPLICATION_JSON)
    boolean usernameExists(@PathParam("username") String username);

    @GET
    @Path("/email-exists/{email}")
    @Produces(APPLICATION_JSON)
    boolean emailExists(@PathParam("email") String email);

    @POST
    @Path("/username-to-jid")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, String> translateUsernamesToJids(Set<String> usernames);
}
