package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.role.RoleChecker;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.rating.UserRatingStore;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {
    private final Clock clock;
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final UserStore userStore;
    private final UserRatingStore ratingStore;
    private final SessionStore sessionStore;

    @Inject
    public UserResource(
            Clock clock,
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            UserStore userStore,
            UserRatingStore ratingStore,
            SessionStore sessionStore) {

        this.clock = clock;
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.ratingStore = ratingStore;
        this.sessionStore = sessionStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUser(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewUser(actorJid, userJid));

        return checkFound(userStore.getUserByJid(userJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<User> getUsers(
            AuthHeader authHeader,
            Optional<Integer> page,
            Optional<String> orderBy,
            Optional<OrderDir> orderDir) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewUserList(actorJid));

        SelectionOptions.Builder options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .orderBy("username").orderDir(OrderDir.ASC);
        page.ifPresent(options::page);
        orderBy.ifPresent(options::orderBy);
        orderDir.ifPresent(options::orderDir);
        return userStore.getUsers(options.build());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean usernameExists(String username) {
        return userStore.getUserByUsername(username).isPresent();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean emailExists(String email) {
        return userStore.getUserByEmail(email).isPresent();
    }

    @Override
    @UnitOfWork
    public User createUser(AuthHeader authHeader, UserData userData) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canCreateUser(actorJid));

        return userStore.createUser(userData);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, UserInfo> getUserInfosByJids(Set<String> jids) {
        Map<String, Integer> ratingsByJid = ratingStore.getRatings(clock.instant(), jids);
        return userStore.getUsersByJids(jids).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> new UserInfo.Builder()
                                .username(e.getValue().getUsername())
                                .rating(Optional.ofNullable(ratingsByJid.get(e.getKey())))
                                .build()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, UserInfo> getPastUserInfosByJids(Set<String> jids) {
        Map<String, Integer> ratingsByJid = ratingStore.getRatings(Instant.ofEpochMilli(0), jids);
        return userStore.getUsersByJids(jids).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> new UserInfo.Builder()
                                .username(e.getValue().getUsername())
                                .rating(Optional.ofNullable(ratingsByJid.get(e.getKey())))
                                .build()));
    }

    // TODO (fushar): tests
    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, String> getUserCountriesByJids(Set<String> jids) {
        return userStore.getUserCountriesByJids(jids);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, User> getUsersByUsernames(Set<String> usernames) {
        return userStore.getUsersByUsernames(usernames);
    }

    @Override
    @UnitOfWork
    public void updateUserPasswords(AuthHeader authHeader, Map<String, String> jidToPasswordMap) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canUpdateUserList(actorJid));
        jidToPasswordMap.forEach(userStore::updateUserPassword);
        jidToPasswordMap.keySet().forEach(sessionStore::deleteSessionsByUserJid);
    }
}
