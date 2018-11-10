package judgels.jophiel.user.password;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.password.PasswordsUpdateResponse;
import judgels.jophiel.api.user.password.UserPasswordService;
import judgels.jophiel.profile.ProfileStore;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserPasswordResource implements UserPasswordService {
    private final Clock clock;
    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private final UserStore userStore;
    private final ProfileStore profileStore;
    private final SessionStore sessionStore;

    @Inject
    public UserPasswordResource(
            Clock clock,
            ActorChecker actorChecker,
            UserRoleChecker roleChecker,
            UserStore userStore,
            ProfileStore profileStore,
            SessionStore sessionStore) {

        this.clock = clock;
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.profileStore = profileStore;
        this.sessionStore = sessionStore;
    }

    @Override
    @UnitOfWork
    public PasswordsUpdateResponse updateUserPasswords(
            AuthHeader authHeader,
            Map<String, String> usernameToPasswordMap) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(usernameToPasswordMap.keySet());
        Map<String, String> jidToPasswordMap = usernameToPasswordMap.keySet()
                .stream()
                .filter(usernameToJidMap::containsKey)
                .collect(Collectors.toMap(usernameToJidMap::get, usernameToPasswordMap::get));

        jidToPasswordMap.forEach(userStore::updateUserPassword);
        jidToPasswordMap.keySet().forEach(sessionStore::deleteSessionsByUserJid);

        Map<String, Profile> jidToProfileMap = profileStore.getProfiles(clock.instant(), jidToPasswordMap.keySet());
        Map<String, Profile> usernameToProfileMap = usernameToJidMap.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> jidToProfileMap.get(e.getValue())));

        return new PasswordsUpdateResponse.Builder()
                .updatedUserProfilesMap(usernameToProfileMap)
                .build();
    }
}
