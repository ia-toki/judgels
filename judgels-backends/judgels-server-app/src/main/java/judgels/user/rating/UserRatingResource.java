package judgels.user.rating;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import judgels.api.user.rating.UserRatingEvent;

@Path("/api/v2/user-rating")
public class UserRatingResource {
    @Inject protected UserRatingStore ratingStore;

    @Inject public UserRatingResource() {}

    @GET
    @Path("/history")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public List<UserRatingEvent> getRatingHistory(@QueryParam("userJid") String userJid) {
        return ratingStore.getUserRatingEvents(userJid);
    }
}
