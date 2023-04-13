package judgels.jerahmeel.chapter.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemService;
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

public class ChapterProblemResource implements ChapterProblemService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ChapterStore chapterStore;
    private final ChapterProblemStore problemStore;
    private final ProblemClient problemClient;
    private final StatsStore statsStore;

    @Inject
    public ChapterProblemResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ChapterStore chapterStore,
            ChapterProblemStore problemStore,
            ProblemClient problemClient,
            StatsStore statsStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.chapterStore = chapterStore;
        this.problemStore = problemStore;
        this.problemClient = problemClient;
        this.statsStore = statsStore;
    }

    @Override
    @UnitOfWork
    public void setProblems(AuthHeader authHeader, String chapterJid, List<ChapterProblemData> data) {
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

    @Override
    @UnitOfWork(readOnly = true)
    public ChapterProblemsResponse getProblems(Optional<AuthHeader> authHeader, String chapterJid) {
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

    @Override
    @UnitOfWork(readOnly = true)
    public ChapterProblemWorksheet getProblemWorksheet(
            Optional<AuthHeader> authHeader,
            String chapterJid,
            String problemAlias,
            Optional<String> language) {

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
                            .from(problemClient.getProgrammingProblemWorksheet(problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        } else {
            return new judgels.jerahmeel.api.chapter.problem.bundle.ChapterProblemWorksheet.Builder()
                    .defaultLanguage(problemInfo.getDefaultLanguage())
                    .languages(problemInfo.getTitlesByLanguage().keySet())
                    .problem(problem)
                    .worksheet(new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                            .from(problemClient.getBundleProblemWorksheetWithoutAnswerKey(problemJid, language))
                            .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                            .build())
                    .build();
        }
    }
}
