package judgels.jophiel.api.user.account;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.jophiel.api.user.User;

@Path("/api/v2/user-account")
public interface UserAccountService {
    @POST
    @Path("/register")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    User registerUser(UserRegistrationData data);

    @POST
    @Path("/register-google")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    User registerGoogleUser(GoogleUserRegistrationData data);

    @POST
    @Path("/activate/{emailCode}")
    void activateUser(@PathParam("emailCode") String emailCode);

    @POST
    @Path("/request-reset-password/{email}")
    void requestToResetPassword(@PathParam("email") String email);

    @POST
    @Path("/resend-activation-email/{email}")
    void resendActivationEmail(@PathParam("email") String email);

    @POST
    @Path("/reset-password")
    @Consumes(APPLICATION_JSON)
    void resetPassword(PasswordResetData data);
}
