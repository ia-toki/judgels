package judgels.jophiel.user.search;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
