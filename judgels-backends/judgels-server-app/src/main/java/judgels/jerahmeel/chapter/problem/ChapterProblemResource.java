package judgels.jerahmeel.chapter.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.api.Verdict;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemWorksheet;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemsResponse;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.resource.ChapterResourceStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jerahmeel.submission.JerahmeelSubmissionStore;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters/{chapterJid}/problems")
public class ChapterProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterResourceStore resourceStore;
    @Inject protected ChapterProblemStore chapterProblemStore;
    @Inject protected ProblemSetProblemStore problemSetProblemStore;
    @Inject protected StatsStore statsStore;
    @Inject @JerahmeelSubmissionStore protected SubmissionStore submissionStore;
    @Inject protected SubmissionSourceBuilder submissionSourceBuilder;
    @Inject protected SandalphonClient sandalphonClient;

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

        Map<String, String> slugToJidMap = sandalphonClient.translateAllowedProblemSlugsToJids(actorJid, slugs);

        List<ChapterProblem> setData = data.stream().filter(cp -> slugToJidMap.containsKey(cp.getSlug())).map(problem ->
                new ChapterProblem.Builder()
                        .alias(problem.getAlias())
                        .problemJid(slugToJidMap.get(problem.getSlug()))
                        .type(problem.getType())
                        .build())
                .collect(Collectors.toList());

        chapterProblemStore.setProblems(chapterJid, setData);
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ChapterProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        List<ChapterProblem> problems = chapterProblemStore.getProblems(chapterJid);

        var problemJids = Lists.transform(problems, ChapterProblem::getProblemJid);
        Map<String, ProblemInfo> problemsMap = sandalphonClient.getProblems(problemJids);
        Map<String, List<List<String>>> problemSetProblemPathsMap = problemSetProblemStore.getProblemSetProblemPathsMap(problemJids);
        Map<String, ProblemProgress> problemProgressesMap = statsStore.getProblemProgressesMap(actorJid, problemJids);

        return new ChapterProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .problemSetProblemPathsMap(problemSetProblemPathsMap)
                .problemProgressesMap(problemProgressesMap)
                .build();
    }

    @GET
    @Path("/{problemAlias}/worksheet")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ChapterProblemWorksheet getProblemWorksheet(
            @Context HttpServletRequest req,
            @Context UriInfo uriInfo,
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        ChapterProblem problem = checkFound(chapterProblemStore.getProblemByAlias(chapterJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = sandalphonClient.getProblem(problemJid);

        Optional<String> reasonNotAllowedToSubmit = authHeader.isPresent()
                ? Optional.empty()
                : Optional.of("You must log in to submit.");

        List<Optional<String>> previousAndNextResourcePaths =
                resourceStore.getPreviousAndNextResourcePathsForProblem(chapterJid, problemAlias);
        List<List<String>> problemSetProblemPaths = problemSetProblemStore.getProblemSetProblemPaths(problemJid);
        ProblemProgress progress = statsStore.getProblemProgressesMap(actorJid, Set.of(problemJid)).get(problemJid);
        Optional<ProblemEditorialInfo> editorial = progress.getVerdict().equals(Verdict.ACCEPTED.getCode())
                ? sandalphonClient.getProblemEditorial(req, uriInfo, problemJid, language)
                : Optional.empty();

        if (problemInfo.getType() == ProblemType.PROGRAMMING) {
            Optional<Submission> lastSubmission = Optional.empty();
            Optional<SubmissionSource> lastSubmissionSource = Optional.empty();

            Optional<Submission> submission = submissionStore.getLatestSubmission(Optional.of(chapterJid), Optional.of(actorJid), Optional.of(problemJid));
            if (submission.isPresent()) {
                lastSubmission = submissionStore.getSubmissionById(submission.get().getId());
                lastSubmissionSource = Optional.of(submissionSourceBuilder.fromPastSubmission(submission.get().getJid(), true));
            }

            return new judgels.jerahmeel.api.chapter.problem.programming.ChapterProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .previousResourcePath(previousAndNextResourcePaths.get(0))
                    .nextResourcePath(previousAndNextResourcePaths.get(1))
                    .worksheet(new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder()
                            .from(sandalphonClient.getProgrammingProblemWorksheet(req, uriInfo, problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .skeletons(sandalphonClient.getProgrammingProblemSkeletons(problemJid))
                    .lastSubmission(lastSubmission)
                    .lastSubmissionSource(lastSubmissionSource)
                    .problemSetProblemPaths(problemSetProblemPaths)
                    .progress(progress)
                    .editorial(editorial)
                    .build();
        } else {
            return new judgels.jerahmeel.api.chapter.problem.bundle.ChapterProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .previousResourcePath(previousAndNextResourcePaths.get(0))
                    .nextResourcePath(previousAndNextResourcePaths.get(1))
                    .worksheet(new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                            .from(sandalphonClient.getBundleProblemWorksheetWithoutAnswerKey(req, uriInfo, problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .progress(progress)
                    .editorial(editorial)
                    .build();
        }
    }
}
