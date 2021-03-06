package judgels.uriel.api.contest.editorial;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/api/v2/contests/{contestJid}/editorial")
public interface ContestEditorialService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestEditorialResponse getEditorial(
            @PathParam("contestJid") String contestJid,
            @QueryParam("language") Optional<String> language);
}
