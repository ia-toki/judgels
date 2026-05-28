package tlx.chapter.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemProgress;
import judgels.chapter.ChapterStore;
import judgels.chapter.problem.ChapterProblemStore;
import judgels.problem.ProblemService;
import judgels.problemset.problem.ProblemSetProblemStore;
import judgels.role.TrainingAdminRoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.stats.StatsStore;
import tlx.api.chapter.problem.ChapterProblem;
import tlx.api.chapter.problem.ChapterProblemData;
import tlx.api.chapter.problem.ChapterProblemsResponse;

@Path("/api/v2/admin/chapters/{chapterJid}/problems")
public class ChapterProblemAdminResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterProblemStore chapterProblemStore;
    @Inject protected ProblemSetProblemStore problemSetProblemStore;
    @Inject protected StatsStore statsStore;
    @Inject protected ProblemService problemService;

    @Inject public ChapterProblemAdminResource() {}

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

        Map<String, String> slugToJidMap = problemService.translateAllowedProblemSlugsToJids(actorJid, slugs);

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
}
