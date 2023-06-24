package judgels.jophiel.user.registration.web;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
