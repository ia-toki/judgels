package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
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
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final UserStore userStore;

    @Inject
    public UserResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            UserStore userStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUser(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewUser(actorJid, userJid));

        return checkFound(userStore.findUserByJid(userJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<User> getUsers(AuthHeader authHeader, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewUserList(actorJid));

        SelectionOptions.Builder options = new SelectionOptions.Builder();
        options.orderDir(OrderDir.DESC);
        page.ifPresent(options::page);
        return userStore.getUsers(options.build());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean usernameExists(String username) {
        return userStore.findUserByUsername(username).isPresent();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean emailExists(String email) {
        return userStore.findUserByEmail(email).isPresent();
    }

    @Override
    @UnitOfWork
    public User createUser(AuthHeader authHeader, UserData userData) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canCreateUser(actorJid));

        return userStore.createUser(userData);
    }

    @Override
    @UnitOfWork
    public Map<String, UserInfo> findUsersByJids(Set<String> jids) {
        return userStore.findUsersByJids(jids).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> new UserInfo.Builder().username(e.getValue().getUsername()).build()));
    }

    @Override
    @UnitOfWork
    public Map<String, User> findUsersByUsernames(Set<String> usernames) {
        return userStore.findUsersByUsernames(usernames);
    }
}
