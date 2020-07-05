package judgels.uriel.contest.file;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import judgels.fs.FileSystem;
import judgels.service.ServiceUtils;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.file.ContestFile;
import judgels.uriel.api.contest.file.ContestFileConfig;
import judgels.uriel.api.contest.file.ContestFileService;
import judgels.uriel.api.contest.file.ContestFilesResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.file.FileFs;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class ContestFileResource implements ContestFileService {
    private final ActorChecker actorChecker;
    private final ContestFileRoleChecker fileRoleChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final FileSystem fileFs;

    @Inject
    public ContestFileResource(
            ActorChecker actorChecker,
            ContestFileRoleChecker fileRoleChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            @FileFs FileSystem fileFs) {

        this.actorChecker = actorChecker;
        this.fileRoleChecker = fileRoleChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.fileFs = fileFs;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestFilesResponse getFiles(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(fileRoleChecker.canSupervise(actorJid, contest));

        boolean canManage = fileRoleChecker.canManage(actorJid, contest);
        ContestFileConfig config = new ContestFileConfig.Builder()
                .canManage(canManage)
                .build();

        List<ContestFile> files = Lists.transform(fileFs.listFilesInDirectory(Paths.get(contestJid)),
                f -> new ContestFile.Builder()
                        .name(f.getName())
                        .size(f.getSize())
                        .lastModifiedTime(f.getLastModifiedTime())
                        .build());

        contestLogger.log(contestJid, "OPEN_FILES");

        return new ContestFilesResponse.Builder()
                .data(files)
                .config(config)
                .build();
    }

    @GET
    @Path("/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadFile(@PathParam("contestJid") String contestJid, @PathParam("filename") String filename) {
        checkFound(contestStore.getContestByJid(contestJid));
        Response response =
                ServiceUtils.buildDownloadResponse(fileFs.getPrivateFileUrl(Paths.get(contestJid, filename)));

        contestLogger.log(contestJid, "DOWNLOAD_FILE", filename);

        return response;
    }

    @POST
    @Path("/")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @HeaderParam(CONTENT_LENGTH) int contentLength,
            @PathParam("contestJid") String contestJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(fileRoleChecker.canManage(actorJid, contest));

        fileFs.uploadPrivateFile(Paths.get(contestJid, fileDetails.getFileName()), fileStream);

        contestLogger.log(contestJid, "UPLOAD_FILE", fileDetails.getFileName());
    }
}
