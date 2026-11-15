package judgels.training.account;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import judgels.api.training.account.AccountPasswordResetData;
import judgels.api.training.account.AccountRegistrationData;
import judgels.api.training.account.GoogleAccountRegistrationData;
import judgels.api.user.User;
import judgels.setting.TrainingAccountChecker;
import judgels.user.UserStore;

@Path("/api/v4/account")
public class AccountResource {
    @Inject protected UserStore userStore;
    @Inject protected AccountRegisterer accountRegisterer;
    @Inject protected AccountPasswordResetter accountPasswordResetter;
    @Inject protected TrainingAccountChecker trainingAccountChecker;

    @Inject public AccountResource() {}

    @POST
    @Path("/register")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User registerUser(AccountRegistrationData data) {
        checkRegistrationEnabled();
        return accountRegisterer.register(data);
    }

    @POST
    @Path("/register-google")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User registerGoogleUser(GoogleAccountRegistrationData data) {
        checkRegistrationEnabled();
        return accountRegisterer.registerGoogleUser(data);
    }

    @POST
    @Path("/activate/{emailCode}")
    @UnitOfWork
    public void activateUser(@PathParam("emailCode") String emailCode) {
        checkRegistrationEnabled();
        accountRegisterer.activate(emailCode);
    }

    @POST
    @Path("/resend-activation-email/{email}")
    @UnitOfWork
    public void resendActivationEmail(@PathParam("email") String email) {
        checkRegistrationEnabled();
        User user = checkFound(userStore.getUserByEmail(email)
                .filter(u -> !userStore.isUserActivated(u.getJid())));
        accountRegisterer.resendActivationEmail(user);
    }

    @POST
    @Path("/request-reset-password/{email}")
    @UnitOfWork
    public void requestResetPassword(@PathParam("email") String email) {
        checkPasswordResetEnabled();
        Optional<User> user = userStore.getUserByEmail(email);

        if (user.isPresent()) {
            accountPasswordResetter.request(user.get(), email);
        }
    }

    @POST
    @Path("/reset-password")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void resetPassword(AccountPasswordResetData data) {
        checkPasswordResetEnabled();
        accountPasswordResetter.reset(data);
    }

    private void checkRegistrationEnabled() {
        if (!trainingAccountChecker.isRegistrationEnabled()) {
            throw new NotFoundException();
        }
    }

    private void checkPasswordResetEnabled() {
        if (!trainingAccountChecker.isPasswordResetEnabled()) {
            throw new NotFoundException();
        }
    }
}
