package judgels.uriel.api.dump;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/dump")
public interface DumpService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    UrielDump exportDump(@HeaderParam(AUTHORIZATION) AuthHeader authHeader);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void importDump(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, UrielDump urielDump);
}
