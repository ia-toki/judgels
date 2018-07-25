package judgels.jophiel.user.profile;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.profile.PublicUserProfile;
import judgels.jophiel.api.user.profile.UserProfile;
import judgels.jophiel.api.user.profile.UserProfileService;
import judgels.jophiel.role.RoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.rating.UserRatingStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserProfileResource implements UserProfileService {
    private final Clock clock;
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final UserStore userStore;
    private final UserProfileStore profileStore;
    private final UserRatingStore ratingStore;

    @Inject
    public UserProfileResource(
            Clock clock,
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            UserStore userStore,
            UserProfileStore profileStore,
            UserRatingStore ratingStore) {

        this.clock = clock;
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.profileStore = profileStore;
        this.ratingStore = ratingStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserProfile getProfile(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewUser(actorJid, userJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return profileStore.getProfile(user.getJid());
    }

    @Override
    @UnitOfWork
    public UserProfile updateProfile(AuthHeader authHeader, String userJid, UserProfile userProfile) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canUpdateUser(actorJid, userJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return profileStore.upsertProfile(user.getJid(), userProfile);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public PublicUserProfile getPublicProfile(String userJid) {
        User user = checkFound(userStore.getUserByJid(userJid));
        Map<String, Integer> ratings = ratingStore.getRatings(clock.instant(), ImmutableSet.of(userJid));
        return profileStore.getProfile(user.getJid()).toPublic(user, Optional.ofNullable(ratings.get(userJid)));
    }
}
