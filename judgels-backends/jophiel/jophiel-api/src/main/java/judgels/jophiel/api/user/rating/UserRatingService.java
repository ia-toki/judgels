package judgels.jophiel.api.user.rating;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users/ratings")
public interface UserRatingService {
    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void updateRatings(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, UserRatingUpdateData userRatingUpdateData);
}
