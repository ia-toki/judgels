package judgels.jophiel.user.rating;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.rating.RatingEvent;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import judgels.jophiel.user.UserRoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserRatingResource implements UserRatingService {
    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private final UserRatingStore ratingStore;

    @Inject
    public UserRatingResource(
            ActorChecker actorChecker,
            UserRoleChecker roleChecker,
            UserRatingStore ratingStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.ratingStore = ratingStore;
    }

    @Override
    @UnitOfWork
    public void updateRatings(AuthHeader authHeader, UserRatingUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        ratingStore.updateRatings(data.getTime(), data.getEventJid(), data.getRatingsMap());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Optional<RatingEvent> getLatestRatingEvent() {
        return ratingStore.getLatestRatingEvent();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public List<UserRatingEvent> getRatingHistory(String userJid) {
        return ratingStore.getUserRatingEvents(userJid);
    }
}
