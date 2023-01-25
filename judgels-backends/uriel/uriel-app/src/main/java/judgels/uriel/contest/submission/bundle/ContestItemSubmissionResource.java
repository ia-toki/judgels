package judgels.uriel.contest.submission.bundle;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.problem.bundle.ProblemWorksheet;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.bundle.ItemSubmissionData;
import judgels.sandalphon.problem.ProblemClient;
import judgels.sandalphon.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.sandalphon.submission.bundle.ItemSubmissionRegrader;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionService;
import judgels.uriel.api.contest.submission.bundle.ContestItemSubmissionsResponse;
import judgels.uriel.api.contest.submission.bundle.ContestSubmissionSummaryResponse;
import judgels.uriel.contest.ContestRoleChecker;
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
    private static final Marker ITEM_SUBMISSION_MARKER = MarkerFactory.getMarker("ITEM_SUBMISSION");

    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestContestantStore contestContestantStore;
    private final ItemSubmissionStore submissionStore;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final ItemSubmissionRegrader itemSubmissionRegrader;
    private final UserClient userClient;
    private final ProblemClient problemClient;

    @Inject
    public ContestItemSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestContestantStore contestContestantStore,
            ItemSubmissionStore submissionStore,
            ContestRoleChecker contestRoleChecker,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestProblemStore problemStore,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ItemSubmissionRegrader itemSubmissionRegrader,
            UserClient userClient,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestContestantStore = contestContestantStore;
        this.submissionStore = submissionStore;
        this.contestRoleChecker = contestRoleChecker;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.problemStore = problemStore;
        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.itemSubmissionRegrader = itemSubmissionRegrader;
        this.userClient = userClient;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork
    public ContestItemSubmissionsResponse getSubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            Optional<String> problemAlias,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);

        Page<ItemSubmission> submissions = submissionStore.getSubmissions(
                contestJid,
                canSupervise ? byUserJid(username) : Optional.of(actorJid),
                byProblemJid(Optional.of(contestJid), Optional.empty(), problemAlias),
                page);

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

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

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

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contestJid, problemJids);

        Set<String> itemJids = submissions.getPage().stream()
                .map(ItemSubmission::getItemJid)
                .collect(Collectors.toSet());

        Map<String, Item> itemsMap = problemClient.getItems(problemJids, itemJids);
        Map<String, Integer> itemNumbersMap = itemsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getNumber().orElse(0))
                );
        Map<String, ItemType> itemTypesMap = itemsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getType())
                );

        return new ContestItemSubmissionsResponse.Builder()
                .data(submissions)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .itemNumbersMap(itemNumbersMap)
                .itemTypesMap(itemTypesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public void createItemSubmission(AuthHeader authHeader, ItemSubmissionData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(data.getContainerJid()));
        ContestProblem problem = checkFound(problemStore.getProblem(data.getContainerJid(), data.getProblemJid()));
        checkAllowed(problemRoleChecker.canSubmit(actorJid, contest, problem, 0));

        Optional<Item> item = problemClient.getItem(data.getProblemJid(), data.getItemJid());
        checkFound(item);

        if (data.getAnswer().trim().isEmpty()) {
            submissionStore.deleteSubmission(
                    data.getContainerJid(), data.getProblemJid(), data.getItemJid(), actorJid);

            LOGGER.info(
                    ITEM_SUBMISSION_MARKER,
                    "Empty answer submitted by {} for item {} in problem {} and contest {}",
                    actorJid, data.getItemJid(), data.getProblemJid(), data.getContainerJid()
            );
        } else {
            Grading grading = itemSubmissionGraderRegistry
                    .get(item.get().getType())
                    .grade(item.get(), data.getAnswer());

            submissionStore.upsertSubmission(
                    data.getContainerJid(),
                    data.getProblemJid(),
                    data.getItemJid(),
                    data.getAnswer(),
                    grading,
                    actorJid
            );

            LOGGER.info(
                    ITEM_SUBMISSION_MARKER,
                    "Answer '{}' submitted by {} for item {} in problem {} and contest {}, verdict {}, score {}",
                    data.getAnswer(), actorJid, data.getItemJid(), data.getProblemJid(), data.getContainerJid(),
                    grading.getVerdict(), grading.getScore()
            );
        }
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, ItemSubmission> getLatestSubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            String problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);

        ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias));

        List<ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserForProblemInContainer(
                contestJid,
                problem.getProblemJid(),
                canSupervise ? byUserJid(username).orElse(actorJid) : actorJid
        );

        return submissions.stream()
                .map(ItemSubmission::withoutGrading)
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestSubmissionSummaryResponse getSubmissionSummary(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        String userJid = canSupervise ? byUserJid(username).orElse(actorJid) : actorJid;

        List<? extends ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserInContainer(
                contestJid,
                userJid);

        boolean canManage = submissionRoleChecker.canManage(actorJid, contest);
        if (!canManage) {
            submissions = submissions.stream().map(ItemSubmission::withoutGrading).collect(Collectors.toList());
        }

        Map<String, ItemSubmission> submissionsByItemJid = submissions.stream()
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));

        List<String> bundleProblemJidsSortedByAlias = problemStore.getProblemJids(contestJid).stream()
                .filter(problemJid -> problemClient.getProblem(problemJid).getType().equals(ProblemType.BUNDLE))
                .collect(Collectors.toList());
        Map<String, String> problemAliasesByProblemJid = problemStore.getProblemAliasesByJids(
                contestJid, ImmutableSet.copyOf(bundleProblemJidsSortedByAlias));

        Map<String, List<String>> itemJidsByProblemJid = new HashMap<>();
        Map<String, ItemType> itemTypesByItemJid = new HashMap<>();
        for (String problemJid : bundleProblemJidsSortedByAlias) {
            ProblemWorksheet worksheet = problemClient.getBundleProblemWorksheet(problemJid, language);
            List<Item> items = worksheet.getItems().stream()
                    .filter(item -> !item.getType().equals(ItemType.STATEMENT))
                    .collect(Collectors.toList());
            items.sort(Comparator.comparingInt(item -> item.getNumber().get()));

            items.stream().forEach(item -> itemTypesByItemJid.put(item.getJid(), item.getType()));

            itemJidsByProblemJid.put(
                    problemJid,
                    items.stream().map(Item::getJid).collect(Collectors.toList())
            );
        }

        Map<String, String> problemNamesByProblemJid = problemClient.getProblemNames(
                ImmutableSet.copyOf(bundleProblemJidsSortedByAlias), language);

        Profile profile = userClient.getProfile(userJid, contest.getBeginTime());

        ContestSubmissionConfig config = new ContestSubmissionConfig.Builder()
                .canSupervise(canSupervise)
                .canManage(canManage)
                .userJids(ImmutableList.of(userJid))
                .problemJids(bundleProblemJidsSortedByAlias)
                .build();

        return new ContestSubmissionSummaryResponse.Builder()
                .profile(profile)
                .config(config)
                .itemJidsByProblemJid(itemJidsByProblemJid)
                .submissionsByItemJid(submissionsByItemJid)
                .itemTypesMap(itemTypesByItemJid)
                .problemAliasesMap(problemAliasesByProblemJid)
                .problemNamesMap(problemNamesByProblemJid)
                .build();
    }

    @Override
    @UnitOfWork
    public void regradeSubmission(AuthHeader authHeader, String submissionJid) {
        String actorJid = actorChecker.check(authHeader);
        ItemSubmission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        Contest contest = checkFound(contestStore.getContestByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canManage(actorJid, contest));

        itemSubmissionRegrader.regradeSubmission(submission);
    }

    @Override
    @UnitOfWork
    public void regradeSubmissions(
            AuthHeader authHeader,
            Optional<String> contestJid,
            Optional<String> username,
            Optional<String> problemJid,
            Optional<String> problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        if (contestJid.isPresent()) {
            Contest contest = checkFound(contestStore.getContestByJid(contestJid.get()));
            checkAllowed(submissionRoleChecker.canManage(actorJid, contest));
        } else {
            checkAllowed(contestRoleChecker.canAdminister(actorJid));
        }

        itemSubmissionRegrader.regradeSubmissions(
                contestJid,
                byUserJid(username),
                byProblemJid(contestJid, problemJid, problemAlias));
    }

    private Optional<String> byUserJid(Optional<String> username) {
        return username.map(u -> userClient.translateUsernameToJid(u).orElse(""));
    }

    private Optional<String> byProblemJid(
            Optional<String> contestJid,
            Optional<String> problemJid,
            Optional<String> problemAlias) {
        if (contestJid.isPresent() && problemAlias.isPresent()) {
            return Optional.of(problemStore
                    .getProblemByAlias(contestJid.get(), problemAlias.get())
                    .map(ContestProblem::getProblemJid)
                    .orElse(""));
        }
        return problemJid;
    }
}
