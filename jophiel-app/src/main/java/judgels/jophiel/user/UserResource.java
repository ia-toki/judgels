package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.role.RoleChecker;
import judgels.persistence.api.Page;
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
        checkAllowed(roleChecker.canReadUser(actorJid, userJid));

        return checkFound(userStore.findUserByJid(userJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<User> getUsers(AuthHeader authHeader, int page, int pageSize) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadUsers(actorJid));

        return userStore.getUsers(page, pageSize);
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
    public void deleteUserAvatar(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canMutateUser(actorJid, userJid));

        checkFound(userStore.updateUserAvatar(userJid, null));
    }

    @Override
    @UnitOfWork
    public Map<String, User> findUsersByJids(Set<String> jids) {
        return userStore.findUsersByJids(jids);
    }

    @Override
    @UnitOfWork
    public Map<String, User> findUsersByUsernames(Set<String> usernames) {
        return userStore.findUsersByUsernames(usernames);
    }
}
