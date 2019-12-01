package judgels.jerahmeel.api.chapter.submission.bundle;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jerahmeel.api.submission.bundle.AnswerSummaryResponse;
import judgels.jerahmeel.api.submission.bundle.ItemSubmissionsResponse;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.ItemSubmissionData;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters/submissions/bundle")
public interface ChapterItemSubmissionService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ItemSubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("chapterJid") String chapterJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("page") Optional<Integer> page);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void createItemSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ItemSubmissionData data);

    @GET
    @Path("/summary")
    @Produces(APPLICATION_JSON)
    AnswerSummaryResponse getAnswerSummary(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("chapterJid") String chapterJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("language") Optional<String> language);

    @GET
    @Path("/answers")
    @Produces(APPLICATION_JSON)
    Map<String, ItemSubmission> getLatestSubmissionsByUserForProblemInChapter(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("chapterJid") String chapterJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") String problemAlias);

    @POST
    @Path("/{submissionJid}/regrade")
    void regradeSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionJid") String submissionJid);

    @POST
    @Path("/regrade")
    void regradeSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("chapterJid") Optional<String> chapterJid,
            @QueryParam("userJid") Optional<String> userJid,
            @QueryParam("problemJid") Optional<String> problemJid);
}
