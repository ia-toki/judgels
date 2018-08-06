package judgels.jophiel;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.service.ServiceVersion;

@Path("/api/v2/version")
public class VersionResource {
    @Inject
    public VersionResource() {}

    @GET
    @Produces(TEXT_PLAIN)
    public String getVersion() {
        return ServiceVersion.INSTANCE;
    }
}
