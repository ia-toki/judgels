package judgels.uriel.contest.file;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.nio.file.Paths;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import judgels.fs.FileSystem;
import judgels.service.ServiceUtils;
import judgels.service.actor.ActorChecker;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.file.FileFs;

@Path("/api/v2/contests/{contestJid}/files")
public class ContestFileResource {
    private final ActorChecker actorChecker;
    private final ContestFileRoleChecker fileRoleChecker;
    private final ContestStore contestStore;
    private final FileSystem fileFs;

    @Inject
    public ContestFileResource(
            ActorChecker actorChecker,
            ContestFileRoleChecker fileRoleChecker,
            ContestStore contestStore,
            @FileFs FileSystem fileFs) {

        this.actorChecker = actorChecker;
        this.fileRoleChecker = fileRoleChecker;
        this.contestStore = contestStore;
        this.fileFs = fileFs;
    }

    @GET
    @Path("/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadFile(@PathParam("contestJid") String contestJid, @PathParam("filename") String filename) {
        String actorJid = actorChecker.check(Optional.empty());
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(fileRoleChecker.canDownloadFiles(actorJid, contest));

        return ServiceUtils.buildDownloadResponse(fileFs.getPublicFileUrl(Paths.get(contestJid, filename)));
    }
}
