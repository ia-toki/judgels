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
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.problem.bundle.ProblemWorksheet;
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
import judgels.uriel.api.contest.submission.bundle.ContestantAnswerSummaryResponse;
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
    private final ContestItemSubmissionStore submissionStore;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestProblemStore problemStore;
    private final ProfileService profileService;
    private final UserSearchService userSearchService;
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
            UserSearchService userSearchService,
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
        this.userSearchService = userSearchService;
        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
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
        Optional<String> filterUserJid;
        if (canSupervise) {
            filterUserJid = username.map(u -> userSearchService.translateUsernamesToJids(ImmutableSet.of(u)).get(u));
        } else {
            filterUserJid = Optional.of(actorJid);
        }

        Optional<String> problemJid = Optional.empty();
        if (problemAlias.isPresent()) {
            ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias.get()));
            problemJid = Optional.of(problem.getProblemJid());
        }

        Page<ItemSubmission> submissions =
                submissionStore.getSubmissions(contestJid, filterUserJid, problemJid, page);

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
    public void createItemSubmission(AuthHeader authHeader, ContestItemSubmissionData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(data.getContestJid()));
        ContestProblem problem = checkFound(problemStore.getProblem(data.getContestJid(), data.getProblemJid()));
        checkAllowed(problemRoleChecker.canSubmit(actorJid, contest, problem, 0));

        Optional<Item> item = problemClient.getItem(data.getProblemJid(), data.getItemJid());
        checkFound(item);

        if (item.get().getType().equals(ItemType.STATEMENT)) {
            LOGGER.debug(
                    "Answer submitted for a STATEMENT item by {} for item {} in problem {} and contest {};"
                    + " submission will be ignored.",
                    actorJid, data.getItemJid(), data.getProblemJid(), data.getContestJid()
            );
        } else if (data.getAnswer().trim().isEmpty()) {
            submissionStore.deleteSubmission(
                    data.getContestJid(), data.getProblemJid(), data.getItemJid(), actorJid);

            LOGGER.info(
                    ITEM_SUBMISSION_MARKER,
                    "Empty answer submitted by {} for item {} in problem {} and contest {}",
                    actorJid, data.getItemJid(), data.getProblemJid(), data.getContestJid()
            );
        } else {
            Grading grading = itemSubmissionGraderRegistry
                    .get(item.get().getType())
                    .grade(item.get(), data.getAnswer());

            submissionStore.upsertSubmission(
                    data.getContestJid(),
                    data.getProblemJid(),
                    data.getItemJid(),
                    data.getAnswer(),
                    grading,
                    actorJid
            );

            LOGGER.info(
                    ITEM_SUBMISSION_MARKER,
                    "Answer '{}' submitted by {} for item {} in problem {} and contest {}, verdict {}, score {}",
                    data.getAnswer(), actorJid, data.getItemJid(), data.getProblemJid(), data.getContestJid(),
                    grading.getVerdict(), grading.getScore()
            );
        }
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, ItemSubmission> getLatestSubmissionsByUserForProblemInContest(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            String problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        String viewedUserJid;
        if (canSupervise && username.isPresent()) {
            viewedUserJid = userSearchService.translateUsernamesToJids(ImmutableSet.of(username.get()))
                    .get(username.get());
        } else {
            viewedUserJid = actorJid;
        }

        ContestProblem problem = checkFound(problemStore.getProblemByAlias(contestJid, problemAlias));

        List<ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserForProblemInContest(
                contestJid,
                problem.getProblemJid(),
                viewedUserJid
        );

        return submissions.stream()
                .map(ItemSubmission::withoutGrading)
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestantAnswerSummaryResponse getAnswerSummaryForContestant(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);
        String viewedUserJid;
        if (canSupervise && username.isPresent()) {
            viewedUserJid = userSearchService.translateUsernamesToJids(ImmutableSet.of(username.get()))
                    .get(username.get());
        } else {
            viewedUserJid = actorJid;
        }

        List<? extends ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserInContest(
                contestJid, viewedUserJid);

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

        Profile profile = profileService.getProfiles(
                ImmutableSet.of(viewedUserJid), contest.getBeginTime()).get(viewedUserJid);

        ContestSubmissionConfig config = new ContestSubmissionConfig.Builder()
                .canSupervise(canSupervise)
                .canManage(canManage)
                .userJids(ImmutableList.of(viewedUserJid))
                .problemJids(bundleProblemJidsSortedByAlias)
                .build();

        return new ContestantAnswerSummaryResponse.Builder()
                .profile(profile)
                .config(config)
                .itemJidsByProblemJid(itemJidsByProblemJid)
                .submissionsByItemJid(submissionsByItemJid)
                .itemTypesMap(itemTypesByItemJid)
                .problemAliasesMap(problemAliasesByProblemJid)
                .problemNamesMap(problemNamesByProblemJid)
                .build();
    }
}
