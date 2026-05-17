package judgels.submission.bundle;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import judgels.api.chapter.problem.ChapterProblem;
import judgels.api.problem.bundle.BundleItem;
import judgels.api.problem.bundle.Item;
import judgels.api.problem.bundle.ItemType;
import judgels.api.problemset.problem.ProblemSetProblem;
import judgels.api.profile.Profile;
import judgels.api.submission.SubmissionConfig;
import judgels.api.submission.bundle.Grading;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.api.submission.bundle.ItemSubmissionData;
import judgels.api.submission.bundle.TrainingItemSubmissionsResponse;
import judgels.api.submission.bundle.TrainingSubmissionSummaryResponse;
import judgels.chapter.problem.ChapterProblemStore;
import judgels.persistence.api.Page;
import judgels.problem.ProblemService;
import judgels.problemset.problem.ProblemSetProblemStore;
import judgels.profile.ProfileStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.submission.SubmissionRoleChecker;
import judgels.submission.SubmissionUtils;
import judgels.training.submission.bundle.TrainingItemSubmissionRegrader;
import judgels.training.submission.bundle.TrainingItemSubmissionStore;
import judgels.user.UserStore;

@Path("/api/v2/submissions/bundle")
public class ItemSubmissionResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject @TrainingItemSubmissionStore protected ItemSubmissionStore submissionStore;
    @Inject protected SubmissionRoleChecker submissionRoleChecker;
    @Inject protected ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    @Inject @TrainingItemSubmissionRegrader protected ItemSubmissionRegrader itemSubmissionRegrader;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;
    @Inject protected ProblemService problemService;
    @Inject protected ItemSubmissionConsumer itemSubmissionConsumer;

    @Inject protected ProblemSetProblemStore problemSetProblemStore;
    @Inject protected ChapterProblemStore chapterProblemStore;

    @Inject public ItemSubmissionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public TrainingItemSubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("containerJid") String containerJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        Optional<String> userJid = username.map(
                u -> userStore.translateUsernamesToJids(ImmutableSet.of(u)).getOrDefault(u, ""));

        Optional<String> problemJid = Optional.empty();
        if (problemAlias.isPresent()) {
            problemJid = Optional.of(getProblemJidByAlias(containerJid, problemAlias.get()).orElse(""));
        }

        Page<ItemSubmission> submissions = submissionStore.getSubmissions(containerJid, userJid, problemJid, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(submissions.getPage(), ItemSubmission::getUserJid);
        var problemJids = Lists.transform(submissions.getPage(), ItemSubmission::getProblemJid);

        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        SubmissionConfig config = new SubmissionConfig.Builder()
                .canManage(canManage)
                .problemJids(problemJids)
                .build();

        Map<String, String> problemAliasesMap = getProblemAliasesMap(containerJid, problemJids);

        var itemJids = Lists.transform(submissions.getPage(), ItemSubmission::getItemJid);
        Map<String, BundleItem> itemsMap = problemService.getItems(problemJids, itemJids);
        Map<String, Integer> itemNumbersMap = itemsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getNumber().orElse(0)));
        Map<String, ItemType> itemTypesMap = itemsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getType()));

        return new TrainingItemSubmissionsResponse.Builder()
                .data(submissions)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .itemNumbersMap(itemNumbersMap)
                .itemTypesMap(itemTypesMap)
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void createItemSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ItemSubmissionData data) {

        String actorJid = actorChecker.check(authHeader);

        Item item = checkFound(problemService.getItem(data.getProblemJid(), data.getItemJid()));

        if (data.getAnswer().trim().isEmpty()) {
            submissionStore.deleteSubmission(
                    data.getContainerJid(), data.getProblemJid(), data.getItemJid(), actorJid);
        } else {
            Grading grading = itemSubmissionGraderRegistry
                    .get(item.getType())
                    .grade(item, data.getAnswer());

            ItemSubmission submission = submissionStore.upsertSubmission(
                    data.getContainerJid(),
                    data.getProblemJid(),
                    data.getItemJid(),
                    data.getAnswer(),
                    grading,
                    actorJid);

            List<ItemSubmission> submissions = submissionStore.getLatestSubmissionsByUserForProblemInContainer(
                    data.getContainerJid(),
                    data.getProblemJid(),
                    actorJid);

            Map<String, Optional<Grading>> itemGradingsMap = new HashMap<>();
            for (BundleItem bundleItem : problemService.getItems(data.getProblemJid())) {
                if (bundleItem.getNumber().isPresent()) {
                    itemGradingsMap.put(bundleItem.getJid(), Optional.empty());
                }
            }
            for (ItemSubmission s : submissions) {
                itemGradingsMap.put(s.getItemJid(), s.getGrading());
            }
            itemSubmissionConsumer.accept(submission, itemGradingsMap);
        }
    }

    @GET
    @Path("/answers")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Map<String, ItemSubmission> getLatestSubmissions(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("containerJid") String containerJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") String problemAlias) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        String userJid;
        if (canManage && username.isPresent()) {
            userJid = checkFound(userStore.translateUsernameToJid(username.get()));
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

    @GET
    @Path("/summary")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public TrainingSubmissionSummaryResponse getSubmissionSummary(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("containerJid") String containerJid,
            @QueryParam("problemJid") Optional<String> problemJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("language") Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        String userJid;
        if (canManage && username.isPresent()) {
            userJid = checkFound(userStore.translateUsernameToJid(username.get()));
        } else {
            userJid = actorJid;
        }

        List<? extends ItemSubmission> submissions;
        List<String> problemJids;
        if (problemJid.isPresent()) {
            problemJids = ImmutableList.of(problemJid.get());
            submissions = submissionStore.getLatestSubmissionsByUserForProblemInContainer(containerJid, problemJid.get(), userJid);
        } else if (problemAlias.isPresent()) {
            String problemJidFromAlias = getProblemJidByAlias(containerJid, problemAlias.get()).orElse("");
            problemJids = List.of(problemJidFromAlias);
            submissions = submissionStore.getLatestSubmissionsByUserForProblemInContainer(containerJid, problemJidFromAlias, userJid);
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
            List<BundleItem> items = problemService.getItems(pJid).stream()
                    .filter(item -> item.getNumber().isPresent())
                    .collect(Collectors.toList());
            items.stream().forEach(item -> itemTypesByItemJid.put(item.getJid(), item.getType()));
            itemJidsByProblemJid.put(pJid, items.stream().map(BundleItem::getJid).collect(Collectors.toList()));
        }

        Map<String, String> problemNamesMap = problemService.getProblemNames(ImmutableSet.copyOf(problemJids), language);
        Profile profile = profileStore.getProfile(userJid);

        SubmissionConfig config = new SubmissionConfig.Builder()
                .canManage(canManage)
                .userJids(ImmutableList.of(userJid))
                .problemJids(problemJids)
                .build();

        return new TrainingSubmissionSummaryResponse.Builder()
                .profile(profile)
                .config(config)
                .itemJidsByProblemJid(itemJidsByProblemJid)
                .submissionsByItemJid(submissionsByItemJid)
                .itemTypesMap(itemTypesByItemJid)
                .problemAliasesMap(problemAliasesMap)
                .problemNamesMap(problemNamesMap)
                .build();
    }

    @POST
    @Path("/{submissionJid}/regrade")
    @UnitOfWork
    public void regradeSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionJid") String submissionJid) {

        String actorJid = actorChecker.check(authHeader);
        ItemSubmission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        itemSubmissionRegrader.regradeSubmission(submission);
    }

    @POST
    @Path("/regrade")
    @UnitOfWork
    public void regradeSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("containerJid") Optional<String> containerJid,
            @QueryParam("userJid") Optional<String> userJid,
            @QueryParam("problemJid") Optional<String> problemJid) {

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

    private Map<String, String> getProblemAliasesMap(String containerJid, Collection<String> problemJids) {
        if (SubmissionUtils.isProblemSet(containerJid)) {
            return problemSetProblemStore.getProblemAliasesByJids(problemJids);
        } else {
            return chapterProblemStore.getProblemAliasesByJids(problemJids);
        }
    }
}
