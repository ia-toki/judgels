package judgels.jerahmeel.chapter.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
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
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.api.Verdict;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemWorksheet;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemsResponse;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.resource.ChapterResourceStore;
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
    @Inject protected ChapterProblemStore problemStore;
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

        var problemJids = Lists.transform(problems, ChapterProblem::getProblemJid);
        Map<String, ProblemInfo> problemsMap = sandalphonClient.getProblems(problemJids);
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
            @Context HttpServletRequest req,
            @Context UriInfo uriInfo,
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid,
            @PathParam("problemAlias") String problemAlias,
            @QueryParam("language") Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        ChapterProblem problem = checkFound(problemStore.getProblemByAlias(chapterJid, problemAlias));
        String problemJid = problem.getProblemJid();
        ProblemInfo problemInfo = sandalphonClient.getProblem(problemJid);

        Optional<String> reasonNotAllowedToSubmit = authHeader.isPresent()
                ? Optional.empty()
                : Optional.of("You must log in to submit.");

        List<Optional<String>> previousAndNextResourcePaths =
                resourceStore.getPreviousAndNextResourcePathsForProblem(chapterJid, problemAlias);
        ProblemProgress progress = statsStore.getProblemProgressesMap(actorJid, Set.of(problemJid)).get(problemJid);
        Optional<ProblemEditorialInfo> editorial = progress.getVerdict().equals(Verdict.ACCEPTED.getCode())
                ? sandalphonClient.getProblemEditorial(problemJid, uriInfo.getBaseUri(), language)
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
