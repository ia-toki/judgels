package judgels.jophiel.user.account;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.account.PasswordResetData;
import judgels.jophiel.user.UserStore;

@Path("/api/v2/user-account")
public class UserAccountResource {
    @Inject protected UserStore userStore;
    @Inject protected Optional<UserPasswordResetter> userPasswordResetter;

    @Inject public UserAccountResource() {}

    @POST
    @Path("/request-reset-password/{email}")
    @UnitOfWork
    public void requestToResetPassword(@PathParam("email") String email) {
        Optional<User> user = userStore.getUserByEmail(email);

        if (user.isPresent()) {
            checkFound(userPasswordResetter).request(user.get(), email);
        }
    }

    @POST
    @Path("/reset-password")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void resetPassword(PasswordResetData data) {
        checkFound(userPasswordResetter).reset(data);
    }
}
