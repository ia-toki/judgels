package judgels.jerahmeel.problem;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problem.ProblemService;
import judgels.jerahmeel.api.problem.ProblemSetProblemInfo;
import judgels.jerahmeel.api.problem.ProblemsResponse;
import judgels.jerahmeel.difficulty.ProblemDifficultyStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.persistence.api.Page;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemResource implements ProblemService {
    private final ActorChecker actorChecker;
    private final ProblemStore problemStore;
    private final StatsStore statsStore;
    private final ProblemDifficultyStore difficultyStore;
    private final ProblemClient problemClient;

    @Inject
    public ProblemResource(
            ActorChecker actorChecker,
            ProblemStore problemStore,
            StatsStore statsStore,
            ProblemDifficultyStore difficultyStore,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.problemStore = problemStore;
        this.statsStore = statsStore;
        this.difficultyStore = difficultyStore;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemsResponse getProblems(
            Optional<AuthHeader> authHeader,
            Set<String> tags,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);

        Set<String> allowedProblemJids = null;
        if (!tags.isEmpty()) {
            allowedProblemJids = problemClient.getProblemJidsByTags(tags);
        }

        Page<ProblemSetProblemInfo> problems = problemStore.getProblems(allowedProblemJids, page);
        Set<String> problemJids = problems.getPage().stream()
                .map(ProblemSetProblemInfo::getProblemJid)
                .collect(Collectors.toSet());

        return new ProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemClient.getProblems(problemJids))
                .problemMetadatasMap(problemClient.getProblemMetadatas(problemJids))
                .problemDifficultiesMap(difficultyStore.getProblemDifficultiesMap(problemJids))
                .problemProgressesMap(statsStore.getProblemProgressesMap(actorJid, problemJids))
                .build();
    }
}
