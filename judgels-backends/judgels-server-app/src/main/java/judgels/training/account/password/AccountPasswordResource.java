package judgels.training.account.password;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.Optional;
import judgels.api.training.account.password.AccountPasswordResetData;
import judgels.api.user.User;
import judgels.setting.TrainingAccountChecker;
import judgels.user.UserStore;

@Path("/api/v4/account/password")
public class AccountPasswordResource {
    @Inject protected UserStore userStore;
    @Inject protected AccountPasswordResetter accountPasswordResetter;
    @Inject protected TrainingAccountChecker trainingAccountChecker;

    @Inject public AccountPasswordResource() {}

    @POST
    @Path("/request-reset/{email}")
    @UnitOfWork
    public void requestToResetPassword(@PathParam("email") String email) {
        checkPasswordResetEnabled();
        Optional<User> user = userStore.getUserByEmail(email);

        if (user.isPresent()) {
            accountPasswordResetter.request(user.get(), email);
        }
    }

    @POST
    @Path("/reset")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void resetPassword(AccountPasswordResetData data) {
        checkPasswordResetEnabled();
        accountPasswordResetter.reset(data);
    }

    private void checkPasswordResetEnabled() {
        if (!trainingAccountChecker.isPasswordResetEnabled()) {
            throw new NotFoundException();
        }
    }
}
