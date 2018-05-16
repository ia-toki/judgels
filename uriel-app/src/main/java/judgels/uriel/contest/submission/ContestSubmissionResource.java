package judgels.uriel.contest.submission;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.SubmissionSource;
import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.api.user.UserService;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.submission.Submission;
import judgels.sandalphon.api.submission.SubmissionWithSource;
import judgels.sandalphon.api.submission.SubmissionWithSourceResponse;
import judgels.sandalphon.submission.SubmissionSourceFetcher;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.ContestSubmissionService;
import judgels.uriel.api.contest.submission.ContestSubmissionsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.role.RoleChecker;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;

public class ContestSubmissionResource implements ContestSubmissionService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final SubmissionSourceFetcher submissionSourceFetcher;
    private final ContestSubmissionStore submissionStore;
    private final ContestProblemStore problemStore;
    private final UserService userService;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestSubmissionResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            SubmissionSourceFetcher submissionSourceFetcher,
            ContestSubmissionStore submissionStore,
            ContestProblemStore problemStore,
            UserService userService,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.submissionSourceFetcher = submissionSourceFetcher;
        this.submissionStore = submissionStore;
        this.problemStore = problemStore;
        this.userService = userService;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestSubmissionsResponse getMySubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(roleChecker.canViewOwnSubmissions(actorJid, contest));

        SelectionOptions.Builder options = new SelectionOptions.Builder();
        options.orderDir(OrderDir.DESC);
        page.ifPresent(options::page);

        Page<Submission> data = submissionStore.getSubmissions(contestJid, actorJid, options.build());
        Set<String> userJids = data.getData().stream().map(Submission::getUserJid).collect(Collectors.toSet());
        Set<String> problemJids = data.getData().stream().map(Submission::getProblemJid).collect(Collectors.toSet());

        return new ContestSubmissionsResponse.Builder()
                .data(data)
                .usersMap(userService.findUsersByJids(userJids))
                .problemAliasesMap(problemStore.findProblemAliasesByJids(contestJid, problemJids))
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionWithSourceResponse getSubmissionWithSourceById(AuthHeader authHeader, long submissionId) {
        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.findSubmissionById(submissionId));
        Contest contest = checkFound(contestStore.findContestByJid(submission.getContainerJid()));
        checkAllowed(roleChecker.canViewSubmission(actorJid, contest, submission.getUserJid()));

        ContestProblem contestProblem =
                checkFound(problemStore.findProblem(contest.getJid(), submission.getProblemJid()));
        ProblemInfo problem =
                clientProblemService.getProblem(sandalphonClientAuthHeader, contestProblem.getProblemJid());

        String userJid = submission.getUserJid();
        UserInfo user = checkFound(Optional.ofNullable(
                userService.findUsersByJids(ImmutableSet.of(userJid)).get(userJid)));

        SubmissionSource source = submissionSourceFetcher.fetchSubmissionSource(submission);
        SubmissionWithSource submissionWithSource = new SubmissionWithSource.Builder()
                .submission(submission)
                .source(source)
                .build();

        return new SubmissionWithSourceResponse.Builder()
                .data(submissionWithSource)
                .user(user)
                .problemAlias(contestProblem.getAlias())
                .problemName(problem.getName())
                .containerName(contest.getName())
                .build();
    }
}
