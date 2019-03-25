package judgels.uriel.api.dump;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/dump")
public interface DumpService {
    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void importDump(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, UrielDump urielDump);
}
