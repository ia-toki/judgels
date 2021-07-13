package judgels.jerahmeel.api.stats;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/api/v2/stats/users")
public interface UserStatsService {
    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    UserTopStatsResponse getTopUserStats(
            @QueryParam("page") Optional<Integer> page,
            @QueryParam("pageSize") Optional<Integer> pageSize);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    UserStats getUserStats(@QueryParam("username") String username);
}
