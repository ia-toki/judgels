package judgels.uriel.contest.submission.bundle;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.problem.ProblemClient;
import judgels.sandalphon.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionData;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionService;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionsResponse;
import judgels.uriel.api.contest.submission.bundle.ContestantAnswersResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ContestItemSubmissionResource implements ContestItemSubmissionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContestItemSubmissionResource.class);

    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestContestantStore contestContestantStore;
    private final ContestItemSubmissionStore submissionStore;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final ProfileService profileService;
    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final ProblemClient problemClient;

    @Inject
    public ContestItemSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestContestantStore contestContestantStore,
            ContestItemSubmissionStore submissionStore,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestProblemStore problemStore,
            ProfileService profileService,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestContestantStore = contestContestantStore;
        this.submissionStore = submissionStore;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.problemStore = problemStore;
        this.profileService = profileService;
        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork
    public ContestItemSubmissionsResponse getSubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        Optional<String> actualUserJid = canSupervise ? userJid : Optional.of(actorJid);

        Page<ItemSubmission> submissions =
                submissionStore.getSubmissions(contestJid, actualUserJid, problemJid, page);

        boolean canManage = submissionRoleChecker.canManage(actorJid, contest);
        if (!canManage) {
            submissions = submissions.mapPage(p -> Lists.transform(p, ItemSubmission::withoutGrading));
        }

        List<String> userJidsSortedByUsername;
        Set<String> userJids;

        List<String> problemJidsSortedByAlias;
        Set<String> problemJids;

        userJids = submissions.getPage().stream().map(ItemSubmission::getUserJid).collect(Collectors.toSet());
        if (canSupervise) {
            userJids.addAll(contestContestantStore.getApprovedContestantJids(contestJid));
            userJidsSortedByUsername = Lists.newArrayList(userJids);

            problemJidsSortedByAlias = problemStore.getProblemJids(contestJid);
            problemJids = ImmutableSet.copyOf(problemJidsSortedByAlias);
        } else {
            userJidsSortedByUsername = Collections.emptyList();

            problemJidsSortedByAlias = Collections.emptyList();
            problemJids = submissions.getPage().stream()
                    .map(ItemSubmission::getProblemJid)
                    .collect(Collectors.toSet());
        }

        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());

        userJidsSortedByUsername.sort((u1, u2) -> {
            String usernameA = profilesMap.containsKey(u1) ? profilesMap.get(u1).getUsername() : u1;
            String usernameB = profilesMap.containsKey(u2) ? profilesMap.get(u2).getUsername() : u2;
            return usernameA.compareTo(usernameB);
        });

        ContestSubmissionConfig config = new ContestSubmissionConfig.Builder()
                .canSupervise(canSupervise)
                .canManage(submissionRoleChecker.canManage(actorJid, contest))
                .userJids(userJidsSortedByUsername)
                .problemJids(problemJidsSortedByAlias)
                .build();

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        return new ContestItemSubmissionsResponse.Builder()
                .data(submissions)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public void createItemSubmission(AuthHeader authHeader, ContestItemSubmissionData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(data.getContestJid()));
        ContestProblem problem = checkFound(problemStore.getProblem(data.getContestJid(), data.getProblemJid()));
        checkAllowed(problemRoleChecker.canSubmit(actorJid, contest, problem, 0));

        Optional<Item> item = problemClient.getItem(data.getProblemJid(), data.getItemJid());
        checkFound(item);

        Grading grading = itemSubmissionGraderRegistry
                .get(item.get().getType())
                .grade(item.get(), data.getAnswer());

        submissionStore.upsertSubmission(data, grading, actorJid);

        Marker itemSubmissionMarker = MarkerFactory.getMarker("ITEM_SUBMISSION");
        LOGGER.info(
                itemSubmissionMarker,
                "{} submitted answer '{}' for item {} in problem {} and contest {}, verdict {}, score {}",
                actorJid, data.getAnswer(), data.getItemJid(), data.getProblemJid(), data.getContestJid(),
                grading.getVerdict(), grading.getScore()
        );
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
