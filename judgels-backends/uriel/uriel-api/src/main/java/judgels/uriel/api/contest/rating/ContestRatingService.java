package judgels.uriel.api.contest.rating;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contest-rating")
public interface ContestRatingService {
    @GET
    @Path("/pending")
    @Produces(APPLICATION_JSON)
    ContestsPendingRatingResponse getContestsPendingRating(@HeaderParam(AUTHORIZATION) AuthHeader authHeader);

    @GET
    @Path("/history")
    @Produces(APPLICATION_JSON)
    ContestRatingHistoryResponse getRatingHistory(@QueryParam("username") String username);
}
