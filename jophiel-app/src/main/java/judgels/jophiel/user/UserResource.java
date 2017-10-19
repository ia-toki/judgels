package judgels.jophiel.user;

import com.palantir.remoting.api.errors.ErrorType;
import com.palantir.remoting.api.errors.ServiceException;
import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.client.api.auth.BasicAuthHeader;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;

public class UserResource implements UserService {
    private final UserStore userStore;

    @Inject
    public UserResource(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork
    public User getUserByJid(BasicAuthHeader authHeader, String userJid) {
        return userStore.findByJid(userJid).orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND));
    }

    @Override
    @UnitOfWork
    public void createUser(BasicAuthHeader authHeader, User user) {
        userStore.insert(user);
    }
}
