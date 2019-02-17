package judgels.uriel.api.contest.problem;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contests/{contestJid}/problems")
public interface ContestProblemService {
    @PUT
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void setProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            List<ContestProblemData> data);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ContestProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid);

    @GET
    @Path("/{problemAlias}/programming/worksheet")
    @Produces(APPLICATION_JSON)
    judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet getProgrammingProblemWorksheet(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language);

    @GET
    @Path("/{problemAlias}/bundle/worksheet")
    @Produces(APPLICATION_JSON)
    judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet getBundleProblemWorksheet(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language);
}
