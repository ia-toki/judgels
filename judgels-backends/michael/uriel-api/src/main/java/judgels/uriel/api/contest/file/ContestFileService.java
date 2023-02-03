package judgels.uriel.api.contest.file;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/{contestJid}/files")
public interface ContestFileService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestFilesResponse getFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid);

//    These endpoints are not representable as JAX-RS methods

//    @GET
//    @Path("/{filename}")
//    Response downloadFile(@PathParam("contestJid") String contestJid, @PathParam("filename") String filename);

//    @POST
//    @Path("/")
//    @Consumes(MULTIPART_FORM_DATA)
//    void uploadFile(
//            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
//            @HeaderParam(CONTENT_LENGTH) int contentLength,
//            @PathParam("contestJid") String contestJid,
//            @FormDataParam("file") InputStream fileStream,
//            @FormDataParam("file") FormDataContentDisposition fileDetails);
}
