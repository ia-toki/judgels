package judgels.uriel.api.contest.submission;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.sandalphon.api.submission.SubmissionWithSourceResponse;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/submissions/contest")
public interface ContestSubmissionService {
    @GET
    @Path("/mine")
    @Produces(APPLICATION_JSON)
    ContestSubmissionsResponse getMySubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("page") Optional<Integer> page);

    @GET
    @Path("/id/{submissionId}")
    @Produces(APPLICATION_JSON)
    SubmissionWithSourceResponse getSubmissionWithSourceById(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionId") long submissionId);
}
