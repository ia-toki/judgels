package judgels.jerahmeel.api.admin;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/admins")
public interface AdminService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    AdminsResponse getAdmins(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("page") Optional<Integer> page);

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    AdminsUpsertResponse upsertAdmins(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, Set<String> usernames);

    @POST
    @Path("/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    AdminsDeleteResponse deleteAdmins(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, Set<String> usernames);
}
