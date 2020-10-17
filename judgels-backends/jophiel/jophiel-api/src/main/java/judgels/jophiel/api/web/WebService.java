package judgels.jophiel.api.web;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/v2/web")
public interface WebService {
    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    WebConfig getWebConfig();
}
