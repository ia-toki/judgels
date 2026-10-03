package judgels.training.archive;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

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
import judgels.api.training.archive.Archive;
import judgels.api.training.archive.ArchiveCreateData;
import judgels.api.training.archive.ArchiveUpdateData;
import judgels.api.training.archive.ArchivesResponse;
import judgels.core.api.AuthHeader;
import judgels.role.TrainingAdminRoleChecker;
import judgels.session.ActorChecker;

@Path("/api/v2/archives")
public class ArchiveResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
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
