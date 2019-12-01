package judgels.jerahmeel.api.chapter.submission.programming;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jerahmeel.api.submission.programming.SubmissionsResponse;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters/submissions/programming")
public interface ChapterSubmissionService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    SubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("chapterJid") String chapterJid,
            @QueryParam("userJid") Optional<String> userJid,
            @QueryParam("problemJid") Optional<String> problemJid,
            @QueryParam("page") Optional<Integer> page);

    @GET
    @Path("/id/{submissionId}")
    @Produces(APPLICATION_JSON)
    SubmissionWithSourceResponse getSubmissionWithSourceById(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionId") long submissionId,
            @QueryParam("language") Optional<String> language);

    //    These endpoints are not representable as JAX-RS methods

    //    @POST
    //    @Path("/")
    //    @Consumes(MULTIPART_FORM_DATA)
    //    void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts);
}
