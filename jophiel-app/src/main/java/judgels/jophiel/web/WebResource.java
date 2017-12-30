package judgels.jophiel.web;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/v2/web")
public class WebResource {
    private final WebConfiguration config;

    @Inject
    public WebResource(WebConfiguration config) {
        this.config = config;
    }

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    public WebConfiguration getConfig() {
        return config;
    }
}
