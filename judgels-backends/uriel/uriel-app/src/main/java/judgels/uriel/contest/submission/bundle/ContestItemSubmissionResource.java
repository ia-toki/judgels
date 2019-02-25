package judgels.uriel.contest.submission.bundle;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ProblemWorksheet;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionData;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionService;
import judgels.uriel.api.contest.submission.bundle.ContestantAnswersResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;

public class ContestItemSubmissionResource implements ContestItemSubmissionService {

    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestItemSubmissionStore submissionStore;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final ClientProblemService clientProblemService;
    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final BasicAuthHeader sandalphonClientAuthHeader;

    @Inject
    public ContestItemSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestItemSubmissionStore submissionStore,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestProblemStore problemStore,
            ClientProblemService clientProblemService,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            @Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.submissionStore = submissionStore;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.problemStore = problemStore;
        this.clientProblemService = clientProblemService;
        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
    }

    @Override
    @UnitOfWork
    public void createItemSubmission(AuthHeader authHeader, ContestItemSubmissionData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(data.getContestJid()));
        ContestProblem problem = checkFound(problemStore.getProblem(data.getContestJid(), data.getProblemJid()));
        checkAllowed(problemRoleChecker.canSubmit(actorJid, contest, problem, 0));

        ProblemWorksheet worksheet = clientProblemService.getBundleProblemWorksheet(
                sandalphonClientAuthHeader,
                data.getProblemJid(),
                Optional.empty());
        Optional<Item> item = worksheet.getItems().stream()
                .filter(i -> data.getItemJid().equals(i.getJid()))
                .findAny();
        checkFound(item);

        Grading grading = itemSubmissionGraderRegistry
                .get(item.get().getType())
                .grade(item.get(), data.getAnswer());

        submissionStore.upsertSubmission(data, grading, actorJid);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, ItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> userJid,
            String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        String actualUserJid = canSupervise ? userJid.orElse(actorJid) : actorJid;

        List<ItemSubmission> submissions = submissionStore
                .getLatestSubmissionsByUserForProblemInContest(contestJid, problemJid, actualUserJid);
        return submissions.stream()
                .map(ItemSubmission::withoutGrading)
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestantAnswersResponse getLatestContestantAnswersInContest(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> userJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        String viewedUserJid = canSupervise ? userJid.orElse(actorJid) : actorJid;

        List<? extends ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserInContest(
                contest.getJid(), viewedUserJid);

        boolean canManage = submissionRoleChecker.canManage(actorJid, contest);
        if (!canManage) {
            submissions = submissions.stream().map(ItemSubmission::withoutGrading).collect(Collectors.toList());
        }

        Set<String> userJids = submissions.stream().map(ItemSubmission::getUserJid).collect(Collectors.toSet());
        Set<String> problemJids = submissions.stream().map(ItemSubmission::getProblemJid).collect(Collectors.toSet());

        ContestSubmissionConfig config = new ContestSubmissionConfig.Builder()
                .canSupervise(canSupervise)
                .canManage(canManage)
                .userJids(userJids)
                .problemJids(problemJids)
                .build();

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        return new ContestantAnswersResponse.Builder()
                .answers(submissions.stream().collect(Collectors.groupingBy(ItemSubmission::getProblemJid)))
                .config(config)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }
}
