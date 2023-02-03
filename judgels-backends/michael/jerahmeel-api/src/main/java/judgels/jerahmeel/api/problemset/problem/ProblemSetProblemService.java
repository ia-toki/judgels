package judgels.jerahmeel.api.problemset.problem;

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

@Path("/api/v2/problemsets/{problemSetJid}/problems")
public interface ProblemSetProblemService {
    @PUT
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void setProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            List<ProblemSetProblemData> data);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ProblemSetProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid);

    @GET
    @Path("/{problemAlias}")
    @Produces(APPLICATION_JSON)
    ProblemSetProblem getProblem(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias);

    @GET
    @Path("/{problemAlias}/worksheet")
    @Produces(APPLICATION_JSON)
    ProblemSetProblemWorksheet getProblemWorksheet(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language);

    @GET
    @Path("/{problemAlias}/report")
    @Produces(APPLICATION_JSON)
    ProblemReportResponse getProblemReport(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias);

    @GET
    @Path("/{problemAlias}/editorial")
    @Produces(APPLICATION_JSON)
    ProblemEditorialResponse getProblemEditorial(
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language);
}
