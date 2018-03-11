package judgels.uriel.api.contest.scoreboard;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests")
public interface ContestScoreboardService {
    @GET
    @Path("/{contestJid}/scoreboard")
    @Produces(APPLICATION_JSON)
    ContestScoreboard getScoreboard(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);

    @GET
    @Path("/{contestJid}/scoreboard/frozen")
    @Produces(APPLICATION_JSON)
    ContestScoreboard getFrozenScoreboard(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);
}
