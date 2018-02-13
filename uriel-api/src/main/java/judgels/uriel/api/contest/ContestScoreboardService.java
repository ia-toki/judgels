package judgels.uriel.api.contest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/api/v2/contests")
public interface ContestScoreboardService {
    @GET
    @Path("/{contestJid}/scoreboard/official")
    @Produces(APPLICATION_JSON)
    ContestScoreboard getOfficialContestScoreboard(@PathParam("contestJid") String contestJid);

    @GET
    @Path("/{contestJid}/scoreboard")
    @Produces(APPLICATION_JSON)
    ContestScoreboard getContestScoreboard(@PathParam("contestJid") String contestJid);

    @POST
    @Path("/{contestJid}/scoreboard/official")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    ContestScoreboard upsertOfficialContestScoreboard(@PathParam("contestJid") String contestJid, String scoreboard);

    @POST
    @Path("/{contestJid}/scoreboard")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    ContestScoreboard upsertContestScoreboard(@PathParam("contestJid") String contestJid, String scoreboard);

}
