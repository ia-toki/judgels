package judgels.problemset.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.contest.ContestInfo;
import judgels.api.problem.ProblemDifficulty;
import judgels.api.problem.ProblemEditorialInfo;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemMetadata;
import judgels.api.problem.ProblemProgress;
import judgels.api.problem.ProblemTopStats;
import judgels.api.problem.ProblemType;
import judgels.api.problemset.ProblemSetErrors;
import judgels.api.problemset.problem.ProblemEditorialResponse;
import judgels.api.problemset.problem.ProblemReportResponse;
import judgels.api.problemset.problem.ProblemSetProblem;
import judgels.api.problemset.problem.ProblemSetProblemData;
import judgels.api.problemset.problem.ProblemSetProblemWorksheet;
import judgels.api.problemset.problem.ProblemSetProblemsResponse;
import judgels.api.profile.Profile;
import judgels.difficulty.ProblemDifficultyStore;
import judgels.jophiel.JophielClient;
import judgels.problemset.ProblemSetStore;
import judgels.role.TrainingAdminRoleChecker;
import judgels.sandalphon.SandalphonClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.stats.StatsStore;
import judgels.uriel.UrielClient;

@Path("/api/v2/problemsets/{problemSetJid}/problems")
public class ProblemSetProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
    @Inject protected ProblemSetStore problemSetStore;
    @Inject protected ProblemSetProblemStore problemStore;
    @Inject protected ProblemDifficultyStore difficultyStore;
    @Inject protected StatsStore statsStore;
    @Inject protected JophielClient jophielClient;
    @Inject protected SandalphonClient sandalphonClient;
    @Inject protected UrielClient urielClient;

    @Inject public ProblemSetProblemResource() {}

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            List<ProblemSetProblemData> data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> aliases = data.stream().map(ProblemSetProblemData::getAlias).collect(Collectors.toSet());
        Set<String> slugs = data.stream().map(ProblemSetProblemData::getSlug).collect(Collectors.toSet());

        checkArgument(data.size() <= 100, "Cannot set more than 100 problems.");
        checkArgument(aliases.size() == data.size(), "Problem aliases must be unique");
        checkArgument(slugs.size() == data.size(), "Problem slugs must be unique");

        Map<String, String> slugToJidMap = sandalphonClient.translateAllowedProblemSlugsToJids(actorJid, slugs);

        Set<String> contestSlugs = data.stream()
                .map(ProblemSetProblemData::getContestSlugs)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Map<String, String> contestSlugToJidMap = urielClient.translateContestSlugsToJids(contestSlugs);

        Set<String> notAllowedContestSlugs = data.stream()
                .map(ProblemSetProblemData::getContestSlugs)
                .flatMap(List::stream)
                .filter(slug -> !contestSlugToJidMap.containsKey(slug))
                .collect(Collectors.toSet());

        if (!notAllowedContestSlugs.isEmpty()) {
            throw ProblemSetErrors.contestSlugsNotAllowed(notAllowedContestSlugs);
        }

        List<ProblemSetProblem> setData = data.stream().filter(cp -> slugToJidMap.containsKey(cp.getSlug())).map(p ->
                new ProblemSetProblem.Builder()
                        .alias(p.getAlias())
                        .problemJid(slugToJidMap.get(p.getSlug()))
                        .type(p.getType())
                        .contestJids(p.getContestSlugs().stream()
                                .filter(contestSlugToJidMap::containsKey)
                                .map(contestSlugToJidMap::get)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        Map<String, Boolean> problemVisibilitiesMap = problemStore.setProblems(problemSetJid, setData);
        sandalphonClient.setProblemVisibilityTagsByJids(problemVisibilitiesMap);
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSetProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        List<ProblemSetProblem> problems = problemStore.getProblems(problemSetJid);

        var problemJids = Lists.transform(problems, ProblemSetProblem::getProblemJid);
        var contestJids = problems.stream()
                .map(ProblemSetProblem::getContestJids)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        return new ProblemSetProblemsResponse.Builder()
                .data(problems)
                .problemsMap(sandalphonClient.getProblems(problemJids))
                .problemMetadatasMap(sandalphonClient.getProblemMetadatas(problemJids))
                .problemDifficultiesMap(difficultyStore.getProblemDifficultiesMap(problemJids))
                .problemProgressesMap(statsStore.getProblemProgressesMap(actorJid, problemJids))
                .contestsMap(roleChecker.isAdmin(actorJid)
                        ? urielClient.getContestsByJids(contestJids)
                        : ImmutableMap.of())
                .build();
    }

    @GET
    @Path("/{problemAlias}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSetProblem getProblem(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias) {

        actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        return checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
    }

    @GET
    @Path("/{problemAlias}/worksheet")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSetProblemWorksheet getProblemWorksheet(
            @Context HttpServletRequest req,
            @Context UriInfo uriInfo,
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language) {

        actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        ProblemSetProblem problem = checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = sandalphonClient.getProblem(problemJid);

        Optional<String> reasonNotAllowedToSubmit = authHeader.isPresent()
                ? Optional.empty()
                : Optional.of("You must log in to submit.");

        if (problemInfo.getType() == ProblemType.PROGRAMMING) {
            return new judgels.api.problemset.problem.programming.ProblemSetProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .worksheet(new judgels.api.problem.programming.ProblemWorksheet.Builder()
                            .from(sandalphonClient.getProgrammingProblemWorksheet(req, uriInfo, problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        } else {
            return new judgels.api.problemset.problem.bundle.ProblemSetProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .worksheet(new judgels.api.problem.bundle.ProblemWorksheet.Builder()
                            .from(sandalphonClient.getBundleProblemWorksheetWithoutAnswerKey(req, uriInfo, problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        }
    }

    @GET
    @Path("/{problemAlias}/report")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemReportResponse getProblemReport(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        ProblemSetProblem problem = checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
        String problemJid = problem.getProblemJid();
        Set<String> problemJids = ImmutableSet.of(problemJid);

        ProblemMetadata metadata = sandalphonClient.getProblemMetadata(problem.getProblemJid());
        ProblemDifficulty difficulty = difficultyStore.getProblemDifficultiesMap(problemJids).get(problemJid);
        ProblemTopStats topStats = statsStore.getProblemTopStats(problemJid);
        ProblemProgress progress = statsStore.getProblemProgressesMap(actorJid, problemJids).get(problemJid);

        Map<String, ContestInfo> contestsMap = urielClient.getContestsByJids(ImmutableSet.copyOf(problem.getContestJids()));
        List<ContestInfo> contests = problem.getContestJids().stream()
                .filter(contestsMap::containsKey)
                .map(contestsMap::get)
                .collect(Collectors.toList());

        Set<String> userJids = new HashSet<>();
        metadata.getSettersMap().values().forEach(userJids::addAll);
        topStats.getTopUsersByScore().forEach(e -> userJids.add(e.getUserJid()));
        topStats.getTopUsersByTime().forEach(e -> userJids.add(e.getUserJid()));
        topStats.getTopUsersByMemory().forEach(e -> userJids.add(e.getUserJid()));
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids);

        return new ProblemReportResponse.Builder()
                .metadata(metadata)
                .difficulty(difficulty)
                .topStats(topStats)
                .progress(progress)
                .contests(contests)
                .profilesMap(profilesMap)
                .build();
    }

    @GET
    @Path("/{problemAlias}/editorial")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemEditorialResponse getProblemEditorial(
            @Context HttpServletRequest req,
            @Context UriInfo uriInfo,
            @PathParam("problemSetJid") String problemSetJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language) {

        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        ProblemSetProblem problem = checkFound(problemStore.getProblemByAlias(problemSetJid, problemAlias));
        ProblemEditorialInfo editorial = checkFound(sandalphonClient.getProblemEditorial(req, uriInfo, problem.getProblemJid(), language));
        return new ProblemEditorialResponse.Builder()
                .editorial(editorial)
                .build();
    }
}
