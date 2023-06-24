package judgels.jerahmeel.archive;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
