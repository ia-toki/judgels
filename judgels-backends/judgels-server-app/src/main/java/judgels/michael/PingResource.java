package judgels.michael;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/v2/ping")
public class PingResource {
    @Inject
    public PingResource() {}

    @GET
    @Produces(TEXT_PLAIN)
    public String ping() {
        return "pong";
    }
}
