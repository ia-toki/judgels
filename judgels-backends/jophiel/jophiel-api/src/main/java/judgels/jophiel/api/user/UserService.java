package judgels.jophiel.api.user;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.OrderDir;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users")
public interface UserService {
    @GET
    @Path("/{userJid}")
    @Produces(APPLICATION_JSON)
    User getUser(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, @PathParam("userJid") String userJid);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    User createUser(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, UserData data);

    @POST
    @Path("/batch-get")
    @Consumes(APPLICATION_JSON)
    @Produces(TEXT_PLAIN)
    String exportUsers(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, List<String> usernames);

    @POST
    @Path("/batch-upsert")
    @Consumes(TEXT_PLAIN)
    @Produces(APPLICATION_JSON)
    UsersUpsertResponse upsertUsers(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, String csv)
        throws IOException;

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    UsersResponse getUsers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("page") Optional<Integer> page,
            @QueryParam("orderBy") Optional<String> orderBy,
            @QueryParam("orderDir") Optional<OrderDir> orderDir);
}
