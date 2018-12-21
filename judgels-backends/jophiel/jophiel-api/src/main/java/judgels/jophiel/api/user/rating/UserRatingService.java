package judgels.jophiel.api.user.rating;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/user-rating")
public interface UserRatingService {
    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void updateRatings(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, UserRatingUpdateData data);

    @GET
    @Path("/history")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    List<UserRatingEvent> getRatingHistory(@QueryParam("userJid") String userJid);
}
