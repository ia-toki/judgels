package judgels.jerahmeel.api.problem;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/v2/problems/tags")
public interface ProblemTagService {
    @GET
    @Produces(APPLICATION_JSON)
    ProblemTagsResponse getProblemTags();
}
