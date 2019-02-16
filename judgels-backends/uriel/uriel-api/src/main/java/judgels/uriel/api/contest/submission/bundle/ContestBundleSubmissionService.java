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
import judgels.sandalphon.api.submission.BundleItemSubmission;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/submissions/bundle")
public interface ContestBundleSubmissionService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestBundleSubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") Optional<String> userJid,
            @QueryParam("problemJid") Optional<String> problemJid,
            @QueryParam("page") Optional<Integer> page);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, ContestBundleSubmissionData data);

    @GET
    @Path("/latest")
    @Produces(APPLICATION_JSON)
    Map<String, BundleItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("problemJid") String problemJid,
            @QueryParam("userJid") Optional<String> userJid);
}
