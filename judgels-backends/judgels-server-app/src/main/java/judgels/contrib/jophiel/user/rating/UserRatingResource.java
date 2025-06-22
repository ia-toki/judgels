package judgels.contrib.jophiel.user.rating;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.rating.UserRatingStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import tlx.jophiel.api.user.rating.UserRatingUpdateData;

@Path("/api/v2/user-rating")
public class UserRatingResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserRatingStore ratingStore;

    @Inject public UserRatingResource() {}

    @POST
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateRatings(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, UserRatingUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        ratingStore.updateRatings(data.getTime(), data.getEventJid(), data.getRatingsMap());
    }

    @GET
    @Path("/history")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public List<UserRatingEvent> getRatingHistory(@QueryParam("userJid") String userJid) {
        return ratingStore.getUserRatingEvents(userJid);
    }
}
