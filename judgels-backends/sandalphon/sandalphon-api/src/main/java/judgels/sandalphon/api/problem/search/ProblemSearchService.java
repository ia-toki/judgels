package judgels.sandalphon.api.problem.search;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/problems")
public interface ProblemSearchService {
    @POST
    @Path("/slug-to-jid")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, String> translateSlugsToJids(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, Set<String> slugs);
}
