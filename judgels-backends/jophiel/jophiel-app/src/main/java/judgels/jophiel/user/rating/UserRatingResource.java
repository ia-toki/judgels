package judgels.jophiel.user.rating;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserRatingResource implements UserRatingService {
    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private final UserStore userStore;
    private final UserRatingStore ratingStore;

    @Inject
    public UserRatingResource(
            ActorChecker actorChecker,
            UserRoleChecker roleChecker,
            UserStore userStore,
            UserRatingStore ratingStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.ratingStore = ratingStore;
    }

    @Override
    @UnitOfWork
    public void updateRatings(AuthHeader authHeader, UserRatingUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        Map<String, String> jidsByUsernamesMap = userStore.translateUsernamesToJids(data.getRatingsMap().keySet());
        Map<String, UserRating> ratingsMap = data.getRatingsMap().entrySet()
                .stream()
                .filter(e ->  jidsByUsernamesMap.containsKey(e.getKey()))
                .collect(Collectors.toMap(e -> jidsByUsernamesMap.get(e.getKey()), e -> e.getValue()));

        ratingStore.updateRatings(data.getTime(), data.getEventJid(), ratingsMap);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public List<UserRatingEvent> getRatingHistory(String userJid) {
        return ratingStore.getRatingEvents(userJid);
    }
}
