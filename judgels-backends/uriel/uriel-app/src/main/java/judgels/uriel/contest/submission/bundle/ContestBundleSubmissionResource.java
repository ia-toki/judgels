package judgels.uriel.contest.submission.bundle;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.submission.BundleSubmission;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.bundle.ContestBundleSubmissionData;
import judgels.uriel.api.contest.submission.bundle.ContestBundleSubmissionService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;

public class ContestBundleSubmissionResource implements ContestBundleSubmissionService {

    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestProblemStore problemStore;
    private final ContestBundleSubmissionStore submissionStore;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ClientProblemService clientProblemService;
    private final BasicAuthHeader sandalphonClientAuthHeader;

    @Inject
    public ContestBundleSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestProblemStore problemStore,
            ContestBundleSubmissionStore submissionStore,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ClientProblemService clientProblemService,
            @Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.problemStore = problemStore;
        this.submissionStore = submissionStore;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.clientProblemService = clientProblemService;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, BundleSubmission> getCurrentProblemSubmissions(
            AuthHeader authHeader,
            String contestJid,
            String problemJid,
            Optional<String> userJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        String actualUserJid = canSupervise ? userJid.orElse(actorJid) : actorJid;

        Map<String, BundleSubmission> submissionsByItemJid = submissionStore
                .getLatestSubmissions(contestJid, problemJid, actualUserJid);
        return submissionsByItemJid;
    }

    @Override
    @UnitOfWork
    public void createBundleSubmission(AuthHeader authHeader, ContestBundleSubmissionData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(data.getContestJid()));
        ContestProblem problem = checkFound(problemStore.getProblem(data.getContestJid(), data.getProblemJid()));

        // TODO(nathanchrs): check whether the given itemJid really exists in the problem
        // TODO(nathanchrs): add per-item submission limit check
        checkAllowed(problemRoleChecker.canSubmit(actorJid, contest, problem, 0));

        submissionStore.upsertSubmission(data, actorJid);
    }
}
