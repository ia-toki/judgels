package judgels.jophiel.user.account;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.account.GoogleUserRegistrationData;
import judgels.jophiel.api.user.account.PasswordResetData;
import judgels.jophiel.api.user.account.UserRegistrationData;
import judgels.jophiel.user.UserStore;

@Path("/api/v2/user-account")
public class UserAccountResource {
    @Inject protected UserStore userStore;
    @Inject protected Optional<UserRegisterer> userRegisterer;
    @Inject protected Optional<UserPasswordResetter> userPasswordResetter;
    @Inject protected UserRegistrationEmailStore userRegistrationEmailStore;

    @Inject public UserAccountResource() {}

    @POST
    @Path("/register")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User registerUser(UserRegistrationData data) {
        return checkFound(userRegisterer).register(data);
    }

    @POST
    @Path("/register-google")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User registerGoogleUser(GoogleUserRegistrationData data) {
        return checkFound(userRegisterer).registerGoogleUser(data);
    }

    @POST
    @Path("/activate/{emailCode}")
    @UnitOfWork
    public void activateUser(@PathParam("emailCode") String emailCode) {
        checkFound(userRegisterer).activate(emailCode);
    }

    @POST
    @Path("/request-reset-password/{email}")
    @UnitOfWork
    public void requestToResetPassword(@PathParam("email") String email) {
        Optional<User> user = userStore
                .getUserByEmail(email)
                .filter(u -> userRegistrationEmailStore.isUserActivated(u.getJid()));

        if (user.isPresent()) {
            checkFound(userPasswordResetter).request(user.get(), email);
        }
    }

    @POST
    @Path("/resend-activation-email/{email}")
    @UnitOfWork
    public void resendActivationEmail(@PathParam("email") String email) {
        User user = checkFound(userStore.getUserByEmail(email)
                .filter(u -> !userRegistrationEmailStore.isUserActivated(u.getJid())));
        checkFound(userRegisterer).resendActivationEmail(user);
    }

    @POST
    @Path("/reset-password")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void resetPassword(PasswordResetData data) {
        checkFound(userPasswordResetter).reset(data);
    }
}
