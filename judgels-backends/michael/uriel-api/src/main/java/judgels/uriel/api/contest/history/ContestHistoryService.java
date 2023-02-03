package judgels.uriel.api.contest.history;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/api/v2/contest-history")
public interface ContestHistoryService {
    @GET
    @Path("/public")
    @Produces(APPLICATION_JSON)
    ContestHistoryResponse getPublicHistory(@QueryParam("username") String username);
}
