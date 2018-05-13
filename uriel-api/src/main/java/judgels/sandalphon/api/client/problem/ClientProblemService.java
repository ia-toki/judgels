package judgels.sandalphon.api.client.problem;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.service.api.client.BasicAuthHeader;

@Path("/api/v2/client/problems")
public interface ClientProblemService {
    @GET
    @Path("/{problemJid}")
    @Produces(APPLICATION_JSON)
    ProblemStatement getProblemStatement(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") Optional<String> language);

    @POST
    @Path("/jids")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, ProblemInfo> findProblemsByJids(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @QueryParam("language") Optional<String> language,
            Set<String> jids);

    @POST
    @Path("/jids/languages")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Set<String> getProblemLanguagesByJids(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            Set<String> jids);
}
