package judgels.jophiel.api.user;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users")
public interface UserService {
    @GET
    @Path("/{userJid}")
    @Produces(APPLICATION_JSON)
    User getUser(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, @PathParam("userJid") String userJid);

    @GET
    @Path("/username/{username}/exists")
    @Produces(APPLICATION_JSON)
    boolean usernameExists(@PathParam("username") String username);

    @GET
    @Path("/email/{email}/exists")
    @Produces(APPLICATION_JSON)
    boolean emailExists(@PathParam("email") String email);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    User createUser(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, UserData userData);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    Page<User> getUsers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("page") Optional<Integer> page,
            @QueryParam("orderBy") Optional<String> orderBy,
            @QueryParam("orderDir") Optional<OrderDir> orderDir);

    @POST
    @Path("/jids")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, UserInfo> findUsersByJids(Set<String> jids);

    @POST
    @Path("/countries")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, String> findUserCountriesByJids(Set<String> jids);

    @POST
    @Path("/usernames")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, User> findUsersByUsernames(Set<String> usernames);

    @POST
    @Path("/passwords")
    @Consumes(APPLICATION_JSON)
    void updateUserPasswords(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, Map<String, String> jidToPasswordMap);
}
