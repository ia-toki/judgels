package tlx.jophiel.user.registration.web;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.Optional;

@Path("/api/v2/users/registration/web")
public class UserRegistrationWebResource {
    @Inject protected Optional<UserRegistrationWebConfig> config;

    @Inject public UserRegistrationWebResource() {}

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    public UserRegistrationWebConfig getConfig() {
        return checkFound(config);
    }
}
