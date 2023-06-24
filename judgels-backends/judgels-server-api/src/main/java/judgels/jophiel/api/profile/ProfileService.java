package judgels.jophiel.api.profile;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.Page;

@Path("/api/v2/profiles")
public interface ProfileService {
    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    Page<Profile> getTopRatedProfiles(
            @QueryParam("page") Optional<Integer> pageNumber,
            @QueryParam("pageSize") Optional<Integer> pageSize);

    @GET
    @Path("/{userJid}/basic")
    @Produces(APPLICATION_JSON)
    BasicProfile getBasicProfile(@PathParam("userJid") String userJid);
}
