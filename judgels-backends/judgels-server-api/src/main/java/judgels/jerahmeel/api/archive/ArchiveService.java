package judgels.jerahmeel.api.archive;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/archives")
public interface ArchiveService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ArchivesResponse getArchives(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Archive createArchive(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ArchiveCreateData data);

    @POST
    @Path("/{archiveJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Archive updateArchive(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("archiveJid") String courseJid,
            ArchiveUpdateData data);
}
