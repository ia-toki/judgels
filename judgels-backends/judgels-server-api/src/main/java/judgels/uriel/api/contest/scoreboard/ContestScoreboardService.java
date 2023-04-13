package judgels.uriel.api.contest.scoreboard;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/{contestJid}/scoreboard")
public interface ContestScoreboardService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    Optional<ContestScoreboardResponse> getScoreboard(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("frozen") boolean frozen,
            @QueryParam("showClosedProblems") boolean showClosedProblems,
            @QueryParam("page") Optional<Integer> page);

    @POST
    @Path("/refresh")
    void refreshScoreboard(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);
}
