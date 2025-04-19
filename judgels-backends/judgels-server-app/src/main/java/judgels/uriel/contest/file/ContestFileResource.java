package judgels.uriel.contest.file;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import judgels.fs.FileSystem;
import judgels.service.ServiceUtils;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.file.ContestFile;
import judgels.uriel.api.contest.file.ContestFileConfig;
import judgels.uriel.api.contest.file.ContestFilesResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.file.FileFs;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api/v2/contests/{contestJid}/files")
public class ContestFileResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestFileRoleChecker fileRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject @FileFs protected FileSystem fileFs;

    @Inject public ContestFileResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestFilesResponse getFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

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
    public Response downloadFile(
            @PathParam("contestJid") String contestJid,
            @PathParam("filename") String filename) {

        checkFound(contestStore.getContestByJid(contestJid));
        Response response =
                ServiceUtils.buildDownloadResponse(fileFs.getPrivateFileUrl(Paths.get(contestJid, filename)));

        contestLogger.log(contestJid, "DOWNLOAD_FILE", filename);

        return response;
    }

    @POST
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
