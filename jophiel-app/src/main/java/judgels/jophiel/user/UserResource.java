package judgels.jophiel.user;

import com.palantir.remoting.api.errors.ErrorType;
import com.palantir.remoting.api.errors.ServiceException;
import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;
import judgels.service.actor.ActorChecker;
import judgels.service.actor.AuthHeader;

public class UserResource implements UserService {
    private final ActorChecker actorChecker;
    private final UserStore userStore;

    @Inject
    public UserResource(ActorChecker actorChecker, UserStore userStore) {
        this.actorChecker = actorChecker;
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork
    public User getUser(String userJid) {
        return userStore.findUserByJid(userJid).orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND));
    }

    @Override
    @UnitOfWork
    public User getUserById(long userId) {
        return userStore.findUserById(userId).orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND));
    }

    @Override
    @UnitOfWork
    public void createUser(User.Data userData) {
        userStore.createUser(userData);
    }

    @Override
    @UnitOfWork
    public void updateUser(AuthHeader authHeader, String userJid, User.Data userData) {
        actorChecker.check(authHeader);
        userStore.updateUser(userJid, userData);
    }
}
