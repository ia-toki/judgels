package judgels.jophiel;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import com.palantir.remoting3.clients.UserAgent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/v2/version")
public class VersionResource {
    private final UserAgent agent;

    @Inject
    public VersionResource(UserAgent agent) {
        this.agent = agent;
    }

    @GET
    @Produces(TEXT_PLAIN)
    public String getVersion() {
        return agent.primary().version();
    }
}
