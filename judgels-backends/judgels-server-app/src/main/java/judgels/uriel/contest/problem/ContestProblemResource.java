package judgels.uriel.contest.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;
import static judgels.service.actor.Actors.GUEST;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;

@Path("/api/v2/contests/{contestJid}/problems")
public class ContestProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestProblemRoleChecker roleChecker;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestProblemStore problemStore;
    @Inject protected ContestModuleStore moduleStore;
    @Inject protected SubmissionStore submissionStore;
    @Inject protected SandalphonClient sandalphonClient;

    @Inject public ContestProblemResource() {}

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            List<ContestProblemData> data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canManage(actorJid, contest));

        Set<String> aliases = data.stream().map(ContestProblemData::getAlias).collect(Collectors.toSet());
        Set<String> slugs = data.stream().map(ContestProblemData::getSlug).collect(Collectors.toSet());

        checkArgument(data.size() <= 100, "Cannot set more than 100 problems.");
        checkArgument(aliases.size() == data.size(), "Problem aliases must be unique");
        checkArgument(slugs.size() == data.size(), "Problem slugs must be unique");

        Map<String, String> slugToJidMap = sandalphonClient.translateAllowedProblemSlugsToJids(actorJid, slugs);

        Set<String> notAllowedSlugs = data.stream()
                .map(ContestProblemData::getSlug)
                .filter(slug -> !slugToJidMap.containsKey(slug))
                .collect(Collectors.toSet());

        if (!notAllowedSlugs.isEmpty()) {
            throw ContestErrors.problemSlugsNotAllowed(notAllowedSlugs);
        }

        List<ContestProblem> setData = data.stream().map(problem ->
                new ContestProblem.Builder()
                        .alias(problem.getAlias())
                        .problemJid(slugToJidMap.get(problem.getSlug()))
                        .status(problem.getStatus())
                        .submissionsLimit(problem.getSubmissionsLimit())
                        .points(problem.getPoints().orElse(0))
                        .build())
                .collect(Collectors.toList());

        problemStore.setProblems(contestJid, setData);

        contestLogger.log(contestJid, "SET_PROBLEMS");
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canView(actorJid, contest));

        List<ContestProblem> problems = problemStore.getProblems(contestJid);
        Set<String> problemJids = problems.stream().map(ContestProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, ProblemInfo> problemsMap = sandalphonClient.getProblems(problemJids);
        Map<String, Long> totalSubmissionsMap =
                submissionStore.getTotalSubmissionsMap(contestJid, actorJid, problemJids);

        boolean canManage = roleChecker.canManage(actorJid, contest);
        ContestProblemConfig config = new ContestProblemConfig.Builder()
                .canManage(canManage)
                .build();

        if (!canManage && !roleChecker.canView(GUEST, contest)) {
            // hide slugs in non-public contests from non-managers.
            problemsMap = problemsMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> new ProblemInfo.Builder()
                                    .from(e.getValue())
                                    .slug(Optional.empty())
                                    .build()));
        }

        contestLogger.log(contestJid, "OPEN_PROBLEMS");

        return new ContestProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .totalSubmissionsMap(totalSubmissionsMap)
                .config(config)
                .build();
    }

    @GET
    @Path("/{problemAlias}/programming/worksheet")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet getProgrammingProblemWorksheet(
            @Context HttpServletRequest req,
            @Context UriInfo uriInfo,
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canView(actorJid, contest));

        ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = sandalphonClient.getProblem(problemJid);

        if (problemInfo.getType() != ProblemType.PROGRAMMING) {
            throw ContestErrors.wrongProblemType(problemInfo.getType());
        }

        long totalSubmissions = submissionStore.getTotalSubmissions(contestJid, actorJid, problemJid);

        Optional<String> reasonNotAllowedToSubmit =
                roleChecker.canSubmit(actorJid, contest, problem, totalSubmissions);

        judgels.sandalphon.api.problem.programming.ProblemWorksheet worksheet =
                sandalphonClient.getProgrammingProblemWorksheet(req, uriInfo, problemJid, language);

        LanguageRestriction contestGradingLanguageRestriction =
                moduleStore.getStyleModuleConfig(contestJid, contest.getStyle()).getGradingLanguageRestriction();
        LanguageRestriction problemGradingLanguageRestriction =
                worksheet.getSubmissionConfig().getGradingLanguageRestriction();
        LanguageRestriction combinedGradingLanguageRestriction =
                LanguageRestriction.combine(contestGradingLanguageRestriction, problemGradingLanguageRestriction);

        judgels.sandalphon.api.problem.programming.ProblemWorksheet finalWorksheet = new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder()
                .from(worksheet)
                .submissionConfig(new ProblemSubmissionConfig.Builder()
                        .from(worksheet.getSubmissionConfig())
                        .gradingLanguageRestriction(combinedGradingLanguageRestriction)
                        .build())
                .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                .build();

        contestLogger.log(contestJid, "OPEN_PROBLEM", null, problemJid);

        return new judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet.Builder()
                .defaultLanguage(problemInfo.getDefaultLanguage())
                .languages(problemInfo.getTitlesByLanguage().keySet())
                .problem(problem)
                .totalSubmissions(totalSubmissions)
                .worksheet(finalWorksheet)
                .build();
    }

    @GET
    @Path("/{problemAlias}/bundle/worksheet")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet getBundleProblemWorksheet(
            @Context HttpServletRequest req,
            @Context UriInfo uriInfo,
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canView(actorJid, contest));

        ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = sandalphonClient.getProblem(problemJid);

        if (problemInfo.getType() != ProblemType.BUNDLE) {
            throw ContestErrors.wrongProblemType(problemInfo.getType());
        }

        long totalSubmissions = submissionStore.getTotalSubmissions(contestJid, actorJid, problemJid);

        Optional<String> reasonNotAllowedToSubmit =
                roleChecker.canSubmit(actorJid, contest, problem, totalSubmissions);

        judgels.sandalphon.api.problem.bundle.ProblemWorksheet worksheet =
                sandalphonClient.getBundleProblemWorksheetWithoutAnswerKey(req, uriInfo, problemJid, language);

        judgels.sandalphon.api.problem.bundle.ProblemWorksheet
                finalWorksheet = new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                .from(worksheet)
                .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                .build();

        contestLogger.log(contestJid, "OPEN_PROBLEM", null, problemJid);

        return new judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet.Builder()
                .defaultLanguage(problemInfo.getDefaultLanguage())
                .languages(problemInfo.getTitlesByLanguage().keySet())
                .problem(problem)
                .totalSubmissions(totalSubmissions)
                .worksheet(finalWorksheet)
                .build();
    }
}
