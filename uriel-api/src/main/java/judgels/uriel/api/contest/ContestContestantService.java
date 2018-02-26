package judgels.uriel.api.contest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.Page;

@Path("/api/v2/contests")
public interface ContestContestantService {
    @GET
    @Path("/{contestJid}/contestants")
    @Produces(APPLICATION_JSON)
    Page<String> getContestants(
            @PathParam("contestJid") String contestJid,
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    @POST
    @Path("/{contestJid}/contestants")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Set<String> addContestants(@PathParam("contestJid") String contestJid, Set<String> contestantJids);

}
