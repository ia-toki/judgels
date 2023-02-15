package judgels.michael;

import java.net.URI;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class IndexResource {
    @Inject
    public IndexResource() {}

    @GET
    public Response index() {
        return Response.seeOther(URI.create("/login")).build();
    }
}
