package judgels.training.account.registration;

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
import judgels.api.training.account.registration.AccountRegistrationData;
import judgels.api.training.account.registration.GoogleAccountRegistrationData;
import judgels.api.user.User;
import judgels.setting.TrainingAccountChecker;
import judgels.user.UserStore;

@Path("/api/v4/account/registration")
public class AccountRegistrationResource {
    @Inject protected UserStore userStore;
    @Inject protected AccountRegisterer accountRegisterer;
    @Inject protected TrainingAccountChecker trainingAccountChecker;

    @Inject public AccountRegistrationResource() {}

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User registerUser(AccountRegistrationData data) {
        checkRegistrationEnabled();
        return accountRegisterer.register(data);
    }

    @POST
    @Path("/google")
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

    private void checkRegistrationEnabled() {
        if (!trainingAccountChecker.isRegistrationEnabled()) {
            throw new NotFoundException();
        }
    }
}
