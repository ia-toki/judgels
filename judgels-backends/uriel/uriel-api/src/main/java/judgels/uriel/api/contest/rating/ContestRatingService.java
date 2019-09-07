package judgels.uriel.api.contest.rating;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contest-rating")
public interface ContestRatingService {
    @GET
    @Path("/{contestJid}/result")
    @Produces(APPLICATION_JSON)
    Map<String, UserRating> getRatingResult(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);

    @GET
    @Path("/history")
    @Produces(APPLICATION_JSON)
    ContestRatingHistoryResponse getRatingHistory(@QueryParam("username") String username);
}
