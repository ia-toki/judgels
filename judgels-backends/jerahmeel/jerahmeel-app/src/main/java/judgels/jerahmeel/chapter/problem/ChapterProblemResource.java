package judgels.jerahmeel.chapter.problem;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemService;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ChapterProblemResource implements ChapterProblemService {
    private final ActorChecker actorChecker;
    private final ChapterStore chapterStore;
    private final ChapterProblemStore chapterProblemStore;
    private final ProblemClient problemClient;

    @Inject
    public ChapterProblemResource(
            ActorChecker actorChecker,
            ChapterStore chapterStore,
            ChapterProblemStore chapterProblemStore,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.chapterStore = chapterStore;
        this.chapterProblemStore = chapterProblemStore;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ChapterProblemsResponse getProblems(Optional<AuthHeader> authHeader, String chapterJid) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        List<ChapterProblem> problems = chapterProblemStore.getProblems(chapterJid);
        Set<String> problemJids = problems.stream().map(ChapterProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, ProblemInfo> problemsMap = problemClient.getProblems(problemJids);

        return new ChapterProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .build();
    }
}
