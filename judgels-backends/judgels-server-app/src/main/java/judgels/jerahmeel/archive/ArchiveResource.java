package judgels.jerahmeel.archive;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.archive.ArchiveUpdateData;
import judgels.jerahmeel.api.archive.ArchivesResponse;
import judgels.jerahmeel.role.RoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/archives")
public class ArchiveResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected ArchiveStore archiveStore;

    @Inject public ArchiveResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ArchivesResponse getArchives(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader) {
        actorChecker.check(authHeader);

        return new ArchivesResponse.Builder()
                .data(archiveStore.getArchives())
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Archive createArchive(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ArchiveCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return archiveStore.createArchive(data);
    }

    @POST
    @Path("/{archiveJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Archive updateArchive(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("archiveJid") String archiveJid,
            ArchiveUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(archiveStore.getArchiveByJid(archiveJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        return archiveStore.updateArchive(archiveJid, data);
    }
}
