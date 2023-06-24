package judgels.jerahmeel.chapter.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
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
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemWorksheet;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemsResponse;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters/{chapterJid}/problems")
public class ChapterProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterProblemStore problemStore;
    @Inject protected ProblemClient problemClient;
    @Inject protected StatsStore statsStore;

    @Inject public ChapterProblemResource() {}

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("chapterJid") String chapterJid,
            List<ChapterProblemData> data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> aliases = data.stream().map(ChapterProblemData::getAlias).collect(Collectors.toSet());
        Set<String> slugs = data.stream().map(ChapterProblemData::getSlug).collect(Collectors.toSet());

        checkArgument(data.size() <= 100, "Cannot set more than 100 problems.");
        checkArgument(aliases.size() == data.size(), "Problem aliases must be unique");
        checkArgument(slugs.size() == data.size(), "Problem slugs must be unique");

        Map<String, String> slugToJidMap = problemClient.translateAllowedSlugsToJids(actorJid, slugs);

        List<ChapterProblem> setData = data.stream().filter(cp -> slugToJidMap.containsKey(cp.getSlug())).map(problem ->
                new ChapterProblem.Builder()
                        .alias(problem.getAlias())
                        .problemJid(slugToJidMap.get(problem.getSlug()))
                        .type(problem.getType())
                        .build())
                .collect(Collectors.toList());

        problemStore.setProblems(chapterJid, setData);
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ChapterProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        List<ChapterProblem> problems = problemStore.getProblems(chapterJid);
        Set<String> problemJids = problems.stream().map(ChapterProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, ProblemInfo> problemsMap = problemClient.getProblems(problemJids);
        Map<String, ProblemProgress> problemProgressesMap = statsStore.getProblemProgressesMap(actorJid, problemJids);

        return new ChapterProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .problemProgressesMap(problemProgressesMap)
                .build();
    }

    @GET
    @Path("/{problemAlias}/worksheet")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ChapterProblemWorksheet getProblemWorksheet(
            @Context UriInfo uriInfo,
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language) {

        actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        ChapterProblem problem = checkFound(problemStore.getProblemByAlias(chapterJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = problemClient.getProblem(problemJid);

        Optional<String> reasonNotAllowedToSubmit = authHeader.isPresent()
                ? Optional.empty()
                : Optional.of("You must log in to submit.");

        if (problemInfo.getType() == ProblemType.PROGRAMMING) {
            return new judgels.jerahmeel.api.chapter.problem.programming.ChapterProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .worksheet(new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder()
                            .from(problemClient.getProgrammingProblemWorksheet(problemJid, uriInfo.getBaseUri(), language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        } else {
            return new judgels.jerahmeel.api.chapter.problem.bundle.ChapterProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .worksheet(new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                            .from(problemClient.getBundleProblemWorksheetWithoutAnswerKey(problemJid, uriInfo.getBaseUri(), language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        }
    }
}
