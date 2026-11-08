package judgels.user.registration.web;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import judgels.core.recaptcha.web.RecaptchaWebConfig;
import judgels.user.registration.UserRegisterer;

@Path("/api/v2/users/registration/web")
public class UserRegistrationWebResource {
    @Inject protected UserRegisterer userRegisterer;
    @Inject protected Optional<RecaptchaWebConfig> recaptchaWebConfig;

    @Inject public UserRegistrationWebResource() {}

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserRegistrationWebConfig getWebConfig() {
        if (!userRegisterer.isEnabled()) {
            throw new NotFoundException();
        }

        return new UserRegistrationWebConfig.Builder()
                .recaptchaEnabled(userRegisterer.isRecaptchaEnabled())
                .recaptchaConfig(recaptchaWebConfig)
                .build();
    }
}
