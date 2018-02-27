package judgels.uriel.api.contest.scoreboard;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/api/v2/contests")
public interface ContestScoreboardService {
    @GET
    @Path("/{contestJid}/scoreboard")
    @Produces(APPLICATION_JSON)
    ContestScoreboard getScoreboard(@PathParam("contestJid") String contestJid);

    @GET
    @Path("/{contestJid}/scoreboard/frozen")
    @Produces(APPLICATION_JSON)
    ContestScoreboard getFrozenScoreboard(@PathParam("contestJid") String contestJid);
}
