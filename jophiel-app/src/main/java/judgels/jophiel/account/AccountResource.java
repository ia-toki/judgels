package judgels.jophiel.account;

import com.palantir.remoting.api.errors.ErrorType;
import com.palantir.remoting.api.errors.ServiceException;
import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.account.AccountService;
import judgels.jophiel.api.account.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;

public class AccountResource implements AccountService {
    private UserStore userStore;
    private SessionStore sessionStore;

    @Inject
    public AccountResource(UserStore userStore, SessionStore sessionStore) {
        this.userStore = userStore;
        this.sessionStore = sessionStore;
    }

    @Override
    @UnitOfWork
    public Session logIn(Credentials credentials) {
        User user = userStore.findUserByUsername(credentials.getUsername())
                .orElseThrow(() -> new ServiceException(ErrorType.PERMISSION_DENIED));

        return sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
    }
}
