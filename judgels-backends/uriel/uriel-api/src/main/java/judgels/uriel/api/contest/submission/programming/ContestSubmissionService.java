package judgels.uriel.api.contest.submission.programming;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionInfo;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/submissions/programming")
public interface ContestSubmissionService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestSubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("page") Optional<Integer> page);

    @GET
    @Path("/id/{submissionId}")
    @Produces(APPLICATION_JSON)
    SubmissionWithSourceResponse getSubmissionWithSourceById(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionId") long submissionId,
            @QueryParam("language") Optional<String> language);

    @GET
    @Path("/info")
    @Produces(APPLICATION_JSON)
    SubmissionInfo getSubmissionInfo(
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") String userJid,
            @QueryParam("problemJid") String problemJid);

    @GET
    @Path("/image")
    @Produces("image/png")
    Response getSubmissionSourceImage(
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") String userJid,
            @QueryParam("problemJid") String problemJid);

    @GET
    @Path("/image/dark")
    @Produces("image/png")
    Response getSubmissionSourceDarkImage(
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") String userJid,
            @QueryParam("problemJid") String problemJid);

    @POST
    @Path("/{submissionJid}/regrade")
    void regradeSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionJid") String submissionJid);

    @POST
    @Path("/regrade")
    void regradeSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias);

    @GET
    @Path("/stats")
    @Produces(APPLICATION_JSON)
    List<Submission> getSubmissionsForStats(
            @QueryParam("contestJid") String contestJid,
            @QueryParam("lastSubmissionId") Optional<Long> lastSubmissionId,
            @QueryParam("limit") Optional<Integer> limit);

//    These endpoints are not representable as JAX-RS methods

//    @POST
//    @Path("/")
//    @Consumes(MULTIPART_FORM_DATA)
//    void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts);

//    @GET
//    @Path("/{submissionJid}/download")
//    @Produces(APPLICATION_OCTET_STREAM)
//    Response downloadSubmission(
//            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
//            @PathParam("submissionJid") String submissionJid);

//    @GET
//    @Path("/download")
//    @Produces(APPLICATION_OCTET_STREAM)
//    Response downloadSubmissions(
//            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
//            @QueryParam("contestJid") String contestJid,
//            @QueryParam("userJid") Optional<String> userJid,
//            @QueryParam("problemJid") Optional<String> problemJid,
//            @QueryParam("lastSubmissionId") Optional<Long> lastSubmissionId,
//            @QueryParam("limit") Optional<Integer> limit);
}
