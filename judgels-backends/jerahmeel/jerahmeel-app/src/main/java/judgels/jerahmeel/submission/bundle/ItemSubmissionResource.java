package judgels.jerahmeel.submission.bundle;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import judgels.jerahmeel.api.submission.bundle.ItemSubmissionService;
import judgels.jerahmeel.api.submission.bundle.ItemSubmissionsResponse;
import judgels.jerahmeel.api.submission.bundle.SubmissionSummaryResponse;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
import judgels.jerahmeel.submission.SubmissionRoleChecker;
import judgels.jerahmeel.submission.SubmissionUtils;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
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

public class ItemSubmissionResource implements ItemSubmissionService {
    private final ActorChecker actorChecker;
    private final ItemSubmissionStore submissionStore;
    private final SubmissionRoleChecker submissionRoleChecker;
    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final ItemSubmissionRegrader itemSubmissionRegrader;
    private final UserClient userClient;
    private final ProblemClient problemClient;

    private final ProblemSetProblemStore problemSetProblemStore;
    private final ChapterProblemStore chapterProblemStore;

    @Inject
    public ItemSubmissionResource(
            ActorChecker actorChecker,
            ItemSubmissionStore submissionStore,
            SubmissionRoleChecker submissionRoleChecker,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ItemSubmissionRegrader itemSubmissionRegrader,
            UserClient userClient,
            ProblemClient problemClient,
            ProblemSetProblemStore problemSetProblemStore,
            ChapterProblemStore chapterProblemStore) {

        this.actorChecker = actorChecker;
        this.submissionStore = submissionStore;
        this.submissionRoleChecker = submissionRoleChecker;
        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.itemSubmissionRegrader = itemSubmissionRegrader;
        this.userClient = userClient;
        this.problemClient = problemClient;
        this.problemSetProblemStore = problemSetProblemStore;
        this.chapterProblemStore = chapterProblemStore;
    }

    @Override
    @UnitOfWork
    public ItemSubmissionsResponse getSubmissions(
            Optional<AuthHeader> authHeader,
            String containerJid,
            Optional<String> username,
            Optional<String> problemAlias,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        Optional<String> userJid = username.map(
                u -> userClient.translateUsernamesToJids(ImmutableSet.of(u)).getOrDefault(u, ""));

        Optional<String> problemJid = Optional.empty();
        if (problemAlias.isPresent()) {
            problemJid = Optional.of(getProblemJidByAlias(containerJid, problemAlias.get()).orElse(""));
        }

        Page<ItemSubmission> submissions = submissionStore.getSubmissions(containerJid, userJid, problemJid, page);

        Set<String> userJids = submissions.getPage().stream().map(ItemSubmission::getUserJid).collect(toSet());
        Set<String> problemJids = submissions.getPage().stream().map(ItemSubmission::getProblemJid).collect(toSet());

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids);

        SubmissionConfig config = new SubmissionConfig.Builder()
                .canManage(canManage)
                .problemJids(problemJids)
                .build();

        Map<String, String> problemAliasesMap = getProblemAliasesMap(containerJid, problemJids);

        Set<String> itemJids = submissions.getPage().stream()
                .map(ItemSubmission::getItemJid)
                .collect(toSet());

        Map<String, Item> itemsMap = problemClient.getItems(problemJids, itemJids);
        Map<String, Integer> itemNumbersMap = itemsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getNumber().orElse(0)));
        Map<String, ItemType> itemTypesMap = itemsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getType()));

        return new ItemSubmissionsResponse.Builder()
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

        Item item = checkFound(problemClient.getItem(data.getProblemJid(), data.getItemJid()));

        if (data.getAnswer().trim().isEmpty()) {
            submissionStore.deleteSubmission(
                    data.getContainerJid(), data.getProblemJid(), data.getItemJid(), actorJid);
        } else {
            Grading grading = itemSubmissionGraderRegistry
                    .get(item.getType())
                    .grade(item, data.getAnswer());

            submissionStore.upsertSubmission(
                    data.getContainerJid(),
                    data.getProblemJid(),
                    data.getItemJid(),
                    data.getAnswer(),
                    grading,
                    actorJid);
        }
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, ItemSubmission> getLatestSubmissions(
            Optional<AuthHeader> authHeader,
            String containerJid,
            Optional<String> username,
            String problemAlias) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        String userJid;
        if (canManage && username.isPresent()) {
            userJid = checkFound(userClient.translateUsernameToJid(username.get()));
        } else {
            userJid = actorJid;
        }

        String problemJid = checkFound(getProblemJidByAlias(containerJid, problemAlias));

        List<ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserForProblemInContainer(
                containerJid,
                problemJid,
                userJid);

        return submissions.stream()
                .map(ItemSubmission::withoutGrading)
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionSummaryResponse getSubmissionSummary(
            AuthHeader authHeader,
            String containerJid,
            Optional<String> problemJid,
            Optional<String> username,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        String userJid;
        if (canManage && username.isPresent()) {
            userJid = checkFound(userClient.translateUsernameToJid(username.get()));
        } else {
            userJid = actorJid;
        }

        List<? extends ItemSubmission> submissions;
        List<String> problemJids;
        if (problemJid.isPresent()) {
            problemJids = ImmutableList.of(problemJid.get());
            submissions = submissionStore
                    .getLatestSubmissionsByUserForProblemInContainer(containerJid, problemJid.get(), userJid);
        } else {
            problemJids = chapterProblemStore.getBundleProblemJids(containerJid);
            submissions = submissionStore.getLatestSubmissionsByUserInContainer(containerJid, userJid);
        }

        Map<String, ItemSubmission> submissionsByItemJid = submissions.stream()
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));

        Map<String, String> problemAliasesMap = getProblemAliasesMap(containerJid, ImmutableSet.copyOf(problemJids));

        Map<String, List<String>> itemJidsByProblemJid = new HashMap<>();
        Map<String, ItemType> itemTypesByItemJid = new HashMap<>();

        for (String pJid : problemJids) {
            ProblemWorksheet worksheet = problemClient.getBundleProblemWorksheet(pJid, language);
            List<Item> items = worksheet.getItems().stream()
                    .filter(item -> !item.getType().equals(ItemType.STATEMENT))
                    .collect(Collectors.toList());
            items.sort(Comparator.comparingInt(item -> item.getNumber().get()));

            items.stream().forEach(item -> itemTypesByItemJid.put(item.getJid(), item.getType()));

            itemJidsByProblemJid.put(pJid, items.stream().map(Item::getJid).collect(Collectors.toList()));
        }

        Map<String, String> problemNamesMap = problemClient.getProblemNames(ImmutableSet.copyOf(problemJids), language);
        Profile profile = userClient.getProfile(userJid);

        SubmissionConfig config = new SubmissionConfig.Builder()
                .canManage(canManage)
                .userJids(ImmutableList.of(userJid))
                .problemJids(problemJids)
                .build();

        return new SubmissionSummaryResponse.Builder()
                .profile(profile)
                .config(config)
                .itemJidsByProblemJid(itemJidsByProblemJid)
                .submissionsByItemJid(submissionsByItemJid)
                .itemTypesMap(itemTypesByItemJid)
                .problemAliasesMap(problemAliasesMap)
                .problemNamesMap(problemNamesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public void regradeSubmission(AuthHeader authHeader, String submissionJid) {
        String actorJid = actorChecker.check(authHeader);
        ItemSubmission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        itemSubmissionRegrader.regradeSubmission(submission);
    }

    @Override
    @UnitOfWork
    public void regradeSubmissions(
            AuthHeader authHeader,
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(submissionRoleChecker.canManage(actorJid));
        itemSubmissionRegrader.regradeSubmissions(containerJid, userJid, problemJid);
    }

    private Optional<String> getProblemJidByAlias(String containerJid, String problemAlias) {
        if (SubmissionUtils.isProblemSet(containerJid)) {
            return problemSetProblemStore.getProblemByAlias(containerJid, problemAlias)
                    .map(ProblemSetProblem::getProblemJid);
        } else {
            return chapterProblemStore.getProblemByAlias(containerJid, problemAlias)
                    .map(ChapterProblem::getProblemJid);
        }
    }

    private Map<String, String> getProblemAliasesMap(String containerJid, Set<String> problemJids) {
        if (SubmissionUtils.isProblemSet(containerJid)) {
            return problemSetProblemStore.getProblemAliasesByJids(problemJids);
        } else {
            return chapterProblemStore.getProblemAliasesByJids(problemJids);
        }
    }
}
