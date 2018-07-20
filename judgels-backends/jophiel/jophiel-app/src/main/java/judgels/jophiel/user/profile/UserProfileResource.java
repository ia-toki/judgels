package judgels.jophiel.user.profile;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.profile.PublicUserProfile;
import judgels.jophiel.api.user.profile.UserProfile;
import judgels.jophiel.api.user.profile.UserProfileService;
import judgels.jophiel.role.RoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserProfileResource implements UserProfileService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final UserStore userStore;
    private final UserProfileStore userProfileStore;

    @Inject
    public UserProfileResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            UserStore userStore,
            UserProfileStore userProfileStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.userProfileStore = userProfileStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserProfile getProfile(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewUser(actorJid, userJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return userProfileStore.getProfile(user.getJid());
    }

    @Override
    @UnitOfWork
    public UserProfile updateProfile(AuthHeader authHeader, String userJid, UserProfile userProfile) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canUpdateUser(actorJid, userJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return userProfileStore.upsertProfile(user.getJid(), userProfile);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public PublicUserProfile getPublicProfile(String userJid) {
        User user = checkFound(userStore.getUserByJid(userJid));
        return userProfileStore.getProfile(user.getJid()).toPublic(user.getUsername());
    }
}
