package judgels.user.account;

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
import judgels.api.user.User;
import judgels.api.user.account.GoogleUserRegistrationData;
import judgels.api.user.account.PasswordResetData;
import judgels.api.user.account.UserRegistrationData;
import judgels.user.UserStore;
import judgels.user.registration.UserRegisterer;
import judgels.user.registration.UserRegistrationEmailStore;

@Path("/api/v2/user-account")
public class UserAccountResource {
    @Inject protected UserStore userStore;
    @Inject protected UserPasswordResetter userPasswordResetter;
    @Inject protected UserRegisterer userRegisterer;
    @Inject protected UserRegistrationEmailStore userRegistrationEmailStore;

    @Inject public UserAccountResource() {}

    @POST
    @Path("/register")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User registerUser(UserRegistrationData data) {
        checkRegistrationEnabled();
        return userRegisterer.register(data);
    }

    @POST
    @Path("/register-google")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User registerGoogleUser(GoogleUserRegistrationData data) {
        checkRegistrationEnabled();
        return userRegisterer.registerGoogleUser(data);
    }

    @POST
    @Path("/activate/{emailCode}")
    @UnitOfWork
    public void activateUser(@PathParam("emailCode") String emailCode) {
        checkRegistrationEnabled();
        userRegisterer.activate(emailCode);
    }

    @POST
    @Path("/resend-activation-email/{email}")
    @UnitOfWork
    public void resendActivationEmail(@PathParam("email") String email) {
        checkRegistrationEnabled();
        User user = checkFound(userStore.getUserByEmail(email)
                .filter(u -> !userRegistrationEmailStore.isUserActivated(u.getJid())));
        userRegisterer.resendActivationEmail(user);
    }

    @POST
    @Path("/request-reset-password/{email}")
    @UnitOfWork
    public void requestToResetPassword(@PathParam("email") String email) {
        checkResetPasswordEnabled();
        Optional<User> user = userStore.getUserByEmail(email);

        if (user.isPresent()) {
            userPasswordResetter.request(user.get(), email);
        }
    }

    @POST
    @Path("/reset-password")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void resetPassword(PasswordResetData data) {
        checkResetPasswordEnabled();
        userPasswordResetter.reset(data);
    }

    private void checkRegistrationEnabled() {
        if (!userRegisterer.isEnabled()) {
            throw new NotFoundException();
        }
    }

    private void checkResetPasswordEnabled() {
        if (!userPasswordResetter.isEnabled()) {
            throw new NotFoundException();
        }
    }
}
