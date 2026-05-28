package tlx.chapter.problem;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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
import judgels.api.problem.ProblemEditorialInfo;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemProgress;
import judgels.api.problem.ProblemType;
import judgels.api.submission.programming.Submission;
import judgels.chapter.ChapterStore;
import judgels.chapter.problem.ChapterProblemStore;
import judgels.chapter.resource.ChapterResourceStore;
import judgels.grading.api.SubmissionSource;
import judgels.grading.api.Verdict;
import judgels.problem.ProblemService;
import judgels.problemset.problem.ProblemSetProblemStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.stats.StatsStore;
import judgels.submission.programming.SubmissionSourceBuilder;
import judgels.submission.programming.SubmissionStore;
import judgels.training.submission.programming.TrainingSubmissionSourceBuilder;
import judgels.training.submission.programming.TrainingSubmissionStore;
import tlx.api.chapter.problem.ChapterProblem;
import tlx.api.chapter.problem.ChapterProblemWorksheet;
import tlx.api.chapter.problem.ChapterProblemsResponse;

@Path("/api/v2/chapters/{chapterJid}/problems")
public class ChapterProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterResourceStore resourceStore;
    @Inject protected ChapterProblemStore chapterProblemStore;
    @Inject protected ProblemSetProblemStore problemSetProblemStore;
    @Inject protected StatsStore statsStore;
    @Inject @TrainingSubmissionStore protected SubmissionStore submissionStore;
    @Inject @TrainingSubmissionSourceBuilder protected SubmissionSourceBuilder submissionSourceBuilder;
    @Inject protected ProblemService problemService;

    @Inject public ChapterProblemResource() {}

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
        Map<String, ProblemInfo> problemsMap = problemService.getProblems(problemJids);
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
        ProblemInfo problemInfo = problemService.getProblem(problemJid);

        Optional<String> reasonNotAllowedToSubmit = authHeader.isPresent()
                ? Optional.empty()
                : Optional.of("You must log in to submit.");

        List<Optional<String>> previousAndNextResourcePaths =
                resourceStore.getPreviousAndNextResourcePathsForProblem(chapterJid, problemAlias);
        List<List<String>> problemSetProblemPaths = problemSetProblemStore.getProblemSetProblemPaths(problemJid);
        ProblemProgress progress = statsStore.getProblemProgressesMap(actorJid, Set.of(problemJid)).get(problemJid);
        Optional<ProblemEditorialInfo> editorial = progress.getVerdict().equals(Verdict.ACCEPTED.getCode())
                ? problemService.getProblemEditorial(req, uriInfo, problemJid, language)
                : Optional.empty();

        if (problemInfo.getType() == ProblemType.PROGRAMMING) {
            Optional<Submission> lastSubmission = Optional.empty();
            Optional<SubmissionSource> lastSubmissionSource = Optional.empty();

            Optional<Submission> submission = submissionStore.getLatestSubmission(Optional.of(chapterJid), Optional.of(actorJid), Optional.of(problemJid));
            if (submission.isPresent()) {
                lastSubmission = submissionStore.getSubmissionById(submission.get().getId());
                lastSubmissionSource = Optional.of(submissionSourceBuilder.fromPastSubmission(submission.get().getJid(), true));
            }

            return new tlx.api.chapter.problem.programming.ChapterProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .previousResourcePath(previousAndNextResourcePaths.get(0))
                    .nextResourcePath(previousAndNextResourcePaths.get(1))
                    .worksheet(new judgels.api.problem.programming.ProblemWorksheet.Builder()
                            .from(problemService.getProgrammingProblemWorksheet(req, uriInfo, problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .skeletons(problemService.getProgrammingProblemSkeletons(problemJid))
                    .lastSubmission(lastSubmission)
                    .lastSubmissionSource(lastSubmissionSource)
                    .problemSetProblemPaths(problemSetProblemPaths)
                    .progress(progress)
                    .editorial(editorial)
                    .build();
        } else {
            return new tlx.api.chapter.problem.bundle.ChapterProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .previousResourcePath(previousAndNextResourcePaths.get(0))
                    .nextResourcePath(previousAndNextResourcePaths.get(1))
                    .worksheet(new judgels.api.problem.bundle.ProblemWorksheet.Builder()
                            .from(problemService.getBundleProblemWorksheetWithoutAnswerKey(req, uriInfo, problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .progress(progress)
                    .editorial(editorial)
                    .build();
        }
    }
}
