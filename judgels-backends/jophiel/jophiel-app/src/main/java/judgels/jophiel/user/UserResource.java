package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserResource implements UserService {
    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private final UserStore userStore;

    @Inject
    public UserResource(ActorChecker actorChecker, UserRoleChecker roleChecker, UserStore userStore) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUser(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

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
        checkAllowed(roleChecker.canAdminister(actorJid));

        return userStore.getUsers(page, orderBy, orderDir);
    }

    @Override
    @UnitOfWork
    public User createUser(AuthHeader authHeader, UserData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return userStore.createUser(data);
    }

    @Override
    @UnitOfWork
    public List<User> createUsers(AuthHeader authHeader, List<UserData> data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return userStore.createUsers(data);
    }

}
