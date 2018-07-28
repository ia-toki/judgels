package judgels.jophiel.user.rating;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import judgels.jophiel.role.RoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserRatingResource implements UserRatingService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final UserStore userStore;
    private final UserRatingStore ratingStore;

    @Inject
    public UserRatingResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            UserStore userStore,
            UserRatingStore ratingStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.ratingStore = ratingStore;
    }

    @Override
    @UnitOfWork
    public void updateRatings(AuthHeader authHeader, UserRatingUpdateData userRatingUpdateData) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canUpdateUserList(actorJid));

        Map<String, User> usersMap = userStore.getUsersByUsernames(userRatingUpdateData.getRatingsMap().keySet());
        Map<String, UserRating> ratingsMap = userRatingUpdateData.getRatingsMap().entrySet()
                .stream()
                .filter(e -> usersMap.containsKey(e.getKey()))
                .collect(Collectors.toMap(e -> usersMap.get(e.getKey()).getJid(), e -> e.getValue()));

        ratingStore.updateRatings(userRatingUpdateData.getTime(), userRatingUpdateData.getEventJid(), ratingsMap);
    }
}
