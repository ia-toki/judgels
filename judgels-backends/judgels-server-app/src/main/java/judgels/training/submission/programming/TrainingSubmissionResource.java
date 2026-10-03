package judgels.training.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.programming.ProblemSubmissionConfig;
import judgels.api.profile.Profile;
import judgels.api.submission.programming.Submission;
import judgels.api.submission.programming.SubmissionData;
import judgels.api.submission.programming.SubmissionWithSource;
import judgels.api.submission.programming.SubmissionWithSourceResponse;
import judgels.api.training.chapter.Chapter;
import judgels.api.training.chapter.problem.ChapterProblem;
import judgels.api.training.problemset.ProblemSet;
import judgels.api.training.problemset.problem.ProblemSetProblem;
import judgels.api.training.submission.TrainingSubmissionConfig;
import judgels.api.training.submission.programming.TrainingSubmissionsResponse;
import judgels.core.api.AuthHeader;
import judgels.grading.api.GradingOptions;
import judgels.grading.api.SubmissionSource;
import judgels.persistence.api.CursorPage;
import judgels.problem.ProblemService;
import judgels.problem.ProblemUtils;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import judgels.submission.programming.SubmissionClient;
import judgels.submission.programming.SubmissionRegrader;
import judgels.submission.programming.SubmissionSourceBuilder;
import judgels.submission.programming.SubmissionStore;
import judgels.training.chapter.ChapterStore;
import judgels.training.chapter.problem.ChapterProblemStore;
import judgels.training.problemset.ProblemSetStore;
import judgels.training.problemset.problem.ProblemSetProblemStore;
import judgels.training.submission.TrainingSubmissionRoleChecker;
import judgels.training.submission.TrainingSubmissionUtils;
import judgels.user.UserStore;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("/api/v4/training/submissions/programming")
public class TrainingSubmissionResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject @TrainingSubmissionStore protected SubmissionStore submissionStore;
    @Inject @TrainingSubmissionSourceBuilder protected SubmissionSourceBuilder submissionSourceBuilder;
    @Inject @TrainingSubmissionClient protected SubmissionClient submissionClient;
    @Inject @TrainingSubmissionRegrader protected SubmissionRegrader submissionRegrader;
    @Inject protected TrainingSubmissionRoleChecker submissionRoleChecker;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;
    @Inject protected ProblemService problemService;

    @Inject protected ProblemSetStore problemSetStore;
    @Inject protected ProblemSetProblemStore problemSetProblemStore;

    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterProblemStore chapterProblemStore;

    @Inject public TrainingSubmissionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public TrainingSubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("containerJid") Optional<String> containerJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemJid") Optional<String> problemJid,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("beforeId") Optional<Long> beforeId,
            @QueryParam("afterId") Optional<Long> afterId) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);

        CursorPage<Submission> submissions = submissionStore.getSubmissionsCursor(
                containerJid,
                byUserJid(username),
                byProblemJid(containerJid, problemJid, problemAlias),
                beforeId,
                afterId,
                PAGE_SIZE);

        var containerJids = Lists.transform(submissions.getPage(), Submission::getContainerJid);
        var userJids = Lists.transform(submissions.getPage(), Submission::getUserJid);
        var problemJids = Lists.transform(submissions.getPage(), Submission::getProblemJid);
        if (containerJid.isPresent() && TrainingSubmissionUtils.isChapter(containerJid.get())) {
            problemJids = chapterProblemStore.getProgrammingProblemJids(containerJid.get());
        }

        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        TrainingSubmissionConfig config = new TrainingSubmissionConfig.Builder()
                .canManage(canManage)
                .problemJids(problemJids)
                .build();

        Map<String, String> problemAliasesMap = new HashMap<>();
        if (!containerJid.isPresent() || TrainingSubmissionUtils.isProblemSet(containerJid.get())) {
            problemAliasesMap.putAll(problemSetProblemStore.getProblemAliasesByJids(problemJids));
        }
        if (!containerJid.isPresent() || TrainingSubmissionUtils.isChapter(containerJid.get())) {
            problemAliasesMap.putAll(chapterProblemStore.getProblemAliasesByJids(problemJids));
        }

        Map<String, String> problemNamesMap = new HashMap<>();
        if (!containerJid.isPresent()) {
            problemNamesMap = problemService.getProblemNames(problemJids, Optional.empty());
        }

        Map<String, String> containerNamesMap = new HashMap<>();
        if (!containerJid.isPresent()) {
            containerNamesMap.putAll(problemSetStore.getProblemSetNamesByJids(containerJids));
            containerNamesMap.putAll(chapterStore.getChapterNamesByJids(containerJids));
        }

        Map<String, List<String>> containerPathsMap = new HashMap<>();
        if (!containerJid.isPresent()) {
            containerPathsMap.putAll(problemSetStore.getProblemSetPathsByJids(containerJids));
            containerPathsMap.putAll(chapterStore.getChapterPathsByJids(containerJids));
        }

        return new TrainingSubmissionsResponse.Builder()
                .data(submissions)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .problemNamesMap(problemNamesMap)
                .containerNamesMap(containerNamesMap)
                .containerPathsMap(containerPathsMap)
                .build();
    }

    @GET
    @Path("/{submissionJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Submission getSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionJid") String submissionJid) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, submission.getUserJid()));

        return submission;
    }

    @GET
    @Path("/id/{submissionId}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public SubmissionWithSourceResponse getSubmissionWithSourceById(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("submissionId") long submissionId,
            @QueryParam("language") Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionById(submissionId));

        String containerJid = submission.getContainerJid();
        String problemJid = submission.getProblemJid();
        String userJid = submission.getUserJid();

        List<String> containerPath;
        String containerName;
        String problemAlias;
        Optional<String> reasonNotAllowedToViewSource;

        if (TrainingSubmissionUtils.isProblemSet(containerJid)) {
            ProblemSet problemSet = checkFound(problemSetStore.getProblemSetByJid(containerJid));
            ProblemSetProblem problem = checkFound(problemSetProblemStore.getProblem(problemSet.getJid(), problemJid));
            containerPath = checkFound(problemSetStore.getProblemSetPathByJid(containerJid));
            containerName = problemSet.getName();
            problemAlias = problem.getAlias();
            reasonNotAllowedToViewSource = submissionRoleChecker.canViewProblemSetSource(actorJid, userJid, problemJid);
        } else {
            Chapter chapter = checkFound(chapterStore.getChapterByJid(containerJid));
            ChapterProblem problem = checkFound(chapterProblemStore.getProblem(problemJid));
            containerPath = checkFound(chapterStore.getChapterPathByJid(containerJid));
            containerName = chapter.getName();
            problemAlias = problem.getAlias();
            reasonNotAllowedToViewSource = submissionRoleChecker.canViewChapterSource(actorJid, userJid, problemJid);
        }

        ProblemInfo problem = problemService.getProblem(submission.getProblemJid());

        Profile profile = checkFound(Optional.ofNullable(profileStore.getProfile(userJid)));

        SubmissionWithSource submissionWithSource;
        if (reasonNotAllowedToViewSource.isPresent()) {
            submissionWithSource = new SubmissionWithSource.Builder()
                    .submission(submission)
                    .reasonNotAllowedToViewSource(reasonNotAllowedToViewSource.get())
                    .build();
        } else {
            SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true);
            submissionWithSource = new SubmissionWithSource.Builder()
                    .submission(submission)
                    .source(source)
                    .build();
        }

        return new SubmissionWithSourceResponse.Builder()
                .data(submissionWithSource)
                .profile(profile)
                .problemAlias(problemAlias)
                .problemName(ProblemUtils.getProblemName(problem, language))
                .containerPath(containerPath)
                .containerName(containerName)
                .build();
    }

    @POST
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Submission createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts) {
        actorChecker.check(authHeader);

        String containerJid = checkNotNull(parts.getField("containerJid"), "containerJid").getValue();
        String problemJid = checkNotNull(parts.getField("problemJid"), "problemJid").getValue();
        String gradingLanguage = checkNotNull(parts.getField("gradingLanguage"), "gradingLanguage").getValue();

        SubmissionData data = new SubmissionData.Builder()
                .problemJid(problemJid)
                .containerJid(containerJid)
                .gradingLanguage(gradingLanguage)
                .build();
        SubmissionSource source = submissionSourceBuilder.fromNewSubmission(parts);
        ProblemSubmissionConfig config = problemService.getProgrammingProblemSubmissionConfig(data.getProblemJid());
        GradingOptions options = new GradingOptions.Builder().shouldRevealEvaluation(true).build();
        Submission submission = submissionClient.submit(data, source, config, options);

        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);

        return submission;
    }

    @POST
    @Path("/{submissionJid}/regrade")
    @UnitOfWork
    public void regradeSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionJid") String submissionJid) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        ProblemSubmissionConfig config = problemService.getProgrammingProblemSubmissionConfig(submission.getProblemJid());
        submissionRegrader.regradeSubmission(submission, config);
    }

    @POST
    @Path("/regrade")
    @UnitOfWork(transactional = false)
    public void regradeSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("containerJid") Optional<String> containerJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemJid") Optional<String> problemJid,
            @QueryParam("problemAlias") Optional<String> problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        Map<String, ProblemSubmissionConfig> configsMap = new HashMap<>();

        for (int pageNumber = 1;; pageNumber++) {
            List<Submission> submissions = submissionStore.getSubmissions(
                    containerJid,
                    byUserJid(username),
                    byProblemJid(containerJid, problemJid, problemAlias),
                    pageNumber,
                    100).getPage();

            if (submissions.isEmpty()) {
                break;
            }

            var problemJids = Lists.transform(submissions, Submission::getProblemJid);
            configsMap.putAll(problemService.getProgrammingProblemSubmissionConfigs(
                    Sets.difference(Set.copyOf(problemJids), configsMap.keySet())));

            submissionRegrader.regradeSubmissions(submissions, configsMap);
        }
    }

    private Optional<String> byUserJid(Optional<String> username) {
        return username.map(u -> userStore.translateUsernameToJid(u).orElse(""));
    }

    private Optional<String> byProblemJid(
            Optional<String> containerJid,
            Optional<String> problemJid,
            Optional<String> problemAlias) {
        if (containerJid.isPresent() && problemAlias.isPresent()) {
            if (TrainingSubmissionUtils.isProblemSet(containerJid.get())) {
                return Optional.of(problemSetProblemStore
                        .getProblemByAlias(containerJid.get(), problemAlias.get())
                        .map(ProblemSetProblem::getProblemJid)
                        .orElse(""));
            }
            if (TrainingSubmissionUtils.isChapter(containerJid.get())) {
                return Optional.of(chapterProblemStore
                        .getProblemByAlias(containerJid.get(), problemAlias.get())
                        .map(ChapterProblem::getProblemJid)
                        .orElse(""));
            }
        }
        return problemJid;
    }
}
