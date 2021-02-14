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
import judgels.sandalphon.api.ProblemMetadata;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.service.api.client.BasicAuthHeader;

@Path("/api/v2/client/problems")
public interface ClientProblemService {
    @GET
    @Path("/{problemJid}")
    @Produces(APPLICATION_JSON)
    ProblemInfo getProblem(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("problemJid") String problemJid);

    @GET
    @Path("/{problemJid}/metadata")
    @Produces(APPLICATION_JSON)
    ProblemMetadata getProblemMetadata(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("problemJid") String problemJid);


    @GET
    @Path("/{problemJid}/editorial")
    @Produces(APPLICATION_JSON)
    ProblemEditorialInfo getProblemEditorial(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") Optional<String> language);

    @GET
    @Path("/{problemJid}/programming/submission-config")
    @Produces(APPLICATION_JSON)
    ProblemSubmissionConfig getProgrammingProblemSubmissionConfig(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("problemJid") String problemJid);

    @GET
    @Path("/{problemJid}/programming/worksheet")
    @Produces(APPLICATION_JSON)
    judgels.sandalphon.api.problem.programming.ProblemWorksheet getProgrammingProblemWorksheet(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") Optional<String> language);

    @GET
    @Path("/{problemJid}/bundle/worksheet")
    @Produces(APPLICATION_JSON)
    judgels.sandalphon.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheet(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") Optional<String> language);

    @POST
    @Path("/jids")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, ProblemInfo> getProblems(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            Set<String> jids);

    @POST
    @Path("/allowed-slug-to-jid")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, String> translateAllowedSlugsToJids(
            @HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader,
            @QueryParam("userJid") String userJid,
            Set<String> slugs);
}
