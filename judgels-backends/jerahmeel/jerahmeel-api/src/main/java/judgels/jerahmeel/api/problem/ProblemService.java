package judgels.jerahmeel.api.problem;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/problems")
public interface ProblemService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("tags") Set<String> tags,
            @QueryParam("page") Optional<Integer> page);

    @GET
    @Path("/tags")
    @Produces(APPLICATION_JSON)
    ProblemTagsResponse getProblemTags();
}
