package judgels.uriel.api.contest.submission.bundle;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/submissions/bundle")
public interface ContestItemSubmissionService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestantAnswersResponse getLatestContestantAnswersInContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") Optional<String> userJid);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void createItemSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ContestItemSubmissionData data);

    @GET
    @Path("/answers")
    @Produces(APPLICATION_JSON)
    Map<String, ItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") Optional<String> userJid,
            @QueryParam("problemJid") String problemJid);
}
