package judgels.uriel.contest.problem;

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
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestContestantProblemsResponse;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.submission.ContestSubmissionStore;
import judgels.uriel.role.RoleChecker;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;

public class ContestProblemResource implements ContestProblemService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestProblemStore problemStore;
    private final ContestSubmissionStore submissionStore;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestProblemResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            ContestProblemStore problemStore,
            ContestSubmissionStore submissionStore,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.problemStore = problemStore;
        this.submissionStore = submissionStore;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantProblemsResponse getMyProblems(
            Optional<AuthHeader> authHeader,
            String contestJid,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(roleChecker.canViewProblems(actorJid, contest));

        List<ContestProblem> problems = problemStore.getProblems(contestJid);
        Set<String> problemJids = problems.stream().map(ContestProblem::getProblemJid).collect(Collectors.toSet());
        Map<String, Long> submissionCounts = submissionStore.countSubmissions(contestJid, actorJid, problemJids);
        List<ContestContestantProblem> contestantProblems = Lists.transform(problems, problem ->
                new ContestContestantProblem.Builder()
                        .problem(problem)
                        .totalSubmissions(submissionCounts.getOrDefault(problem.getProblemJid(), 0L))
                        .build());
        Map<String, String> problemNamesMap = clientProblemService.findProblemsByJids(
                sandalphonClientAuthHeader,
                language,
                problemJids).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getName()));

        return new ContestContestantProblemsResponse.Builder()
                .data(contestantProblems)
                .problemNamesMap(problemNamesMap)
                .build();
    }
}
