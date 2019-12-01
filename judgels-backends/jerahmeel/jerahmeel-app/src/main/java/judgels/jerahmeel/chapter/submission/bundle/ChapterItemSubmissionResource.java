package judgels.jerahmeel.chapter.submission.bundle;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
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
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.submission.bundle.ChapterItemSubmissionService;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import judgels.jerahmeel.api.submission.bundle.AnswerSummaryResponse;
import judgels.jerahmeel.api.submission.bundle.ItemSubmissionsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jerahmeel.submission.SubmissionRoleChecker;
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
import judgels.sandalphon.api.submission.bundle.ItemSubmissionData;
import judgels.sandalphon.problem.ProblemClient;
import judgels.sandalphon.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.sandalphon.submission.bundle.ItemSubmissionRegrader;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ChapterItemSubmissionResource implements ChapterItemSubmissionService {
    private final ActorChecker actorChecker;
    private final ChapterStore chapterStore;
    private final ItemSubmissionStore submissionStore;
    private final SubmissionRoleChecker submissionRoleChecker;
    private final ChapterProblemStore problemStore;
    private final ProfileService profileService;
    private final UserSearchService userSearchService;
    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final ItemSubmissionRegrader itemSubmissionRegrader;
    private final ProblemClient problemClient;

    @Inject
    public ChapterItemSubmissionResource(
            ActorChecker actorChecker,
            ChapterStore chapterStore,
            ItemSubmissionStore submissionStore,
            SubmissionRoleChecker submissionRoleChecker,
            ChapterProblemStore problemStore,
            ProfileService profileService,
            UserSearchService userSearchService,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ItemSubmissionRegrader itemSubmissionRegrader,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.chapterStore = chapterStore;
        this.submissionStore = submissionStore;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemStore = problemStore;
        this.profileService = profileService;
        this.userSearchService = userSearchService;
        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.itemSubmissionRegrader = itemSubmissionRegrader;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork
    public ItemSubmissionsResponse getSubmissions(
            AuthHeader authHeader,
            String chapterJid,
            Optional<String> username,
            Optional<String> problemAlias,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Chapter chapter = checkFound(chapterStore.getChapterByJid(chapterJid));

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        Optional<String> actualUserJid;
        if (canManage) {
            actualUserJid = username.map(u -> userSearchService.translateUsernamesToJids(
                    ImmutableSet.of(u)).getOrDefault(u, ""));
        } else {
            actualUserJid = Optional.of(actorJid);
        }

        Optional<String> problemJid = Optional.empty();
        if (problemAlias.isPresent()) {
            Optional<ChapterProblem> problem = problemStore.getProblemByAlias(chapterJid, problemAlias.get());
            problemJid = Optional.of(problem.isPresent() ? problem.get().getProblemJid() : "");
        }

        Page<ItemSubmission> submissions =
                submissionStore.getSubmissions(chapterJid, actualUserJid, problemJid, page);

        Set<String> userJids =
                submissions.getPage().stream().map(ItemSubmission::getUserJid).collect(Collectors.toSet());
        Set<String> problemJids = submissions.getPage().stream()
                .map(ItemSubmission::getProblemJid)
                .collect(Collectors.toSet());

        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids);

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(chapterJid, problemJids);

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

        return new ItemSubmissionsResponse.Builder()
                .data(submissions)
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
        checkFound(chapterStore.getChapterByJid(data.getContainerJid()));
        checkFound(problemStore.getProblem(data.getContainerJid(), data.getProblemJid()));

        Optional<Item> item = problemClient.getItem(data.getProblemJid(), data.getItemJid());
        checkFound(item);

        if (data.getAnswer().trim().isEmpty()) {
            submissionStore.deleteSubmission(
                    data.getContainerJid(), data.getProblemJid(), data.getItemJid(), actorJid);
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
        }
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, ItemSubmission> getLatestSubmissionsByUserForProblemInChapter(
            AuthHeader authHeader,
            String chapterJid,
            Optional<String> username,
            String problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        String viewedUserJid;
        if (canManage && username.isPresent()) {
            Map<String, String> userJidsMap = userSearchService.translateUsernamesToJids(
                    ImmutableSet.of(username.get()));
            viewedUserJid = checkFound(Optional.ofNullable(userJidsMap.get(username.get())));
        } else {
            viewedUserJid = actorJid;
        }

        ChapterProblem problem = checkFound(problemStore.getProblemByAlias(chapterJid, problemAlias));

        List<ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserForProblemInContainer(
                chapterJid,
                problem.getProblemJid(),
                viewedUserJid
        );

        return submissions.stream()
                .map(ItemSubmission::withoutGrading)
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public AnswerSummaryResponse getAnswerSummary(
            AuthHeader authHeader,
            String chapterJid,
            Optional<String> username,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        String viewedUserJid;
        if (canManage && username.isPresent()) {
            Map<String, String> userJidsMap = userSearchService.translateUsernamesToJids(
                    ImmutableSet.of(username.get()));
            viewedUserJid = checkFound(Optional.ofNullable(userJidsMap.get(username.get())));
        } else {
            viewedUserJid = actorJid;
        }

        List<? extends ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserInContainer(
                chapterJid, viewedUserJid);

        Map<String, ItemSubmission> submissionsByItemJid = submissions.stream()
                .collect(Collectors.toMap(ItemSubmission::getItemJid, Function.identity()));

        List<String> bundleProblemJidsSortedByAlias = problemStore.getProblemJids(chapterJid).stream()
                .filter(problemJid -> problemClient.getProblem(problemJid).getType().equals(ProblemType.BUNDLE))
                .collect(Collectors.toList());
        Map<String, String> problemAliasesByProblemJid = problemStore.getProblemAliasesByJids(
                chapterJid, ImmutableSet.copyOf(bundleProblemJidsSortedByAlias));

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

        Profile profile = profileService.getProfile(viewedUserJid);

        SubmissionConfig config = new SubmissionConfig.Builder()
                .canManage(canManage)
                .problemJids(bundleProblemJidsSortedByAlias)
                .build();

        return new AnswerSummaryResponse.Builder()
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
        checkFound(chapterStore.getChapterByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        itemSubmissionRegrader.regradeSubmission(submission);
    }

    @Override
    @UnitOfWork
    public void regradeSubmissions(
            AuthHeader authHeader,
            Optional<String> chapterJid,
            Optional<String> userJid,
            Optional<String> problemJid) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(submissionRoleChecker.canManage(actorJid));
        if (chapterJid.isPresent()) {
            checkFound(chapterStore.getChapterByJid(chapterJid.get()));
        }

        itemSubmissionRegrader.regradeSubmissions(chapterJid, userJid, problemJid);
    }
}
