package judgels.jophiel.user.registration.web;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/api/v2/users/registration/web")
public class UserRegistrationWebResource {
    @Inject protected UserRegistrationWebConfig config;

    @Inject public UserRegistrationWebResource() {}

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    public UserRegistrationWebConfig getConfig() {
        return config;
    }
}
