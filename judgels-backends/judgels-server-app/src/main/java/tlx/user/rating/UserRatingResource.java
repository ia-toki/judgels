package tlx.user.rating;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.user.UserRoleChecker;
import judgels.user.rating.UserRatingStore;
import tlx.api.user.rating.UserRatingUpdateData;

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
}
