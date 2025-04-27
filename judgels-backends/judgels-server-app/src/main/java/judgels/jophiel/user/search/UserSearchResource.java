package judgels.jophiel.user.search;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.Map;
import java.util.Set;
import judgels.jophiel.user.UserStore;

@Path("/api/v2/user-search")
public class UserSearchResource {
    @Inject protected UserStore userStore;

    @Inject public UserSearchResource() {}

    @GET
    @Path("/username-exists/{username}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public boolean usernameExists(@PathParam("username") String username) {
        return userStore.getUserByUsername(username).isPresent();
    }

    @GET
    @Path("/email-exists/{email}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public boolean emailExists(@PathParam("email") String email) {
        return userStore.getUserByEmail(email).isPresent();
    }

    @POST
    @Path("/username-to-jid")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Map<String, String> translateUsernamesToJids(Set<String> usernames) {
        return userStore.translateUsernamesToJids(usernames);
    }
}
