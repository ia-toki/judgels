package judgels.jerahmeel.problemset.problem;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemService;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemsResponse;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemSetProblemResource implements ProblemSetProblemService {
    private final ActorChecker actorChecker;
    private final ProblemSetStore problemSetStore;
    private final ProblemSetProblemStore problemSetProblemStore;
    private final ProblemClient problemClient;

    @Inject
    public ProblemSetProblemResource(
            ActorChecker actorChecker,
            ProblemSetStore problemSetStore,
            ProblemSetProblemStore problemSetProblemStore,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.problemSetStore = problemSetStore;
        this.problemSetProblemStore = problemSetProblemStore;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemSetProblemsResponse getProblems(Optional<AuthHeader> authHeader, String problemSetJid) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));

        List<ProblemSetProblem> problems = problemSetProblemStore.getProblems(problemSetJid);
        Set<String> problemJids = problems.stream().map(ProblemSetProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, ProblemInfo> problemsMap = problemClient.getProblems(problemJids);

        return new ProblemSetProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemsMap)
                .build();
    }
}
