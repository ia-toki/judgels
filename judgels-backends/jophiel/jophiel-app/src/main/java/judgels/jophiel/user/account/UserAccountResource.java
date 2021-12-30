package judgels.jophiel.user.account;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.account.GoogleUserRegistrationData;
import judgels.jophiel.api.user.account.PasswordResetData;
import judgels.jophiel.api.user.account.UserAccountService;
import judgels.jophiel.api.user.account.UserRegistrationData;
import judgels.jophiel.user.UserStore;

public class UserAccountResource implements UserAccountService {
    private final UserStore userStore;
    private final Optional<UserRegisterer> userRegisterer;
    private final Optional<UserPasswordResetter> userPasswordResetter;
    private final UserRegistrationEmailStore userRegistrationEmailStore;

    @Inject
    public UserAccountResource(
            UserStore userStore,
            Optional<UserRegisterer> userRegisterer,
            Optional<UserPasswordResetter> userPasswordResetter,
            UserRegistrationEmailStore userRegistrationEmailStore) {

        this.userStore = userStore;
        this.userRegisterer = userRegisterer;
        this.userPasswordResetter = userPasswordResetter;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
    }

    @Override
    @UnitOfWork
    public User registerUser(UserRegistrationData data) {
        return checkFound(userRegisterer).register(data);
    }

    @Override
    @UnitOfWork
    public User registerGoogleUser(GoogleUserRegistrationData data) {
        return checkFound(userRegisterer).registerGoogleUser(data);
    }

    @Override
    @UnitOfWork
    public void activateUser(String emailCode) {
        checkFound(userRegisterer).activate(emailCode);
    }

    @Override
    @UnitOfWork
    public void requestToResetPassword(String email) {
        Optional<User> user = userStore
                .getUserByEmail(email)
                .filter(u -> userRegistrationEmailStore.isUserActivated(u.getJid()));

        if (user.isPresent()) {
            checkFound(userPasswordResetter).request(user.get(), email);
        }
    }

    @Override
    @UnitOfWork
    public void resendActivationEmail(String email) {
        User user = checkFound(userStore.getUserByEmail(email)
                .filter(u -> !userRegistrationEmailStore.isUserActivated(u.getJid())));
        checkFound(userRegisterer).resendActivationEmail(user);
    }

    @Override
    @UnitOfWork
    public void resetPassword(PasswordResetData data) {
        checkFound(userPasswordResetter).reset(data);
    }
}
