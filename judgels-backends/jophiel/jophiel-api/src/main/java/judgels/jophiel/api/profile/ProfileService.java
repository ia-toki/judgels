package judgels.jophiel.api.profile;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.Page;

@Path("/api/v2/profiles")
public interface ProfileService {
    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, Profile> getProfiles(Set<String> userJids);

    // TODO (fushar): accept time as parameter
    @POST
    @Path("/past")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, Profile> getPastProfiles(Set<String> userJids);

    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    Page<Profile> getTopRatedProfiles(
            @QueryParam("page") Optional<Integer> page,
            @QueryParam("pageSize") Optional<Integer> pageSize);

    @GET
    @Path("/{userJid}/basic")
    @Produces(APPLICATION_JSON)
    BasicProfile getBasicProfile(@PathParam("userJid") String userJid);
}
