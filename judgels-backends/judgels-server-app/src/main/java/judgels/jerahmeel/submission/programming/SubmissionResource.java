package judgels.jerahmeel.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.buildDarkImageResponseFromText;
import static judgels.service.ServiceUtils.buildLightImageResponseFromText;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.GradingOptions;
import judgels.gabriel.api.SubmissionSource;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import judgels.jerahmeel.api.submission.programming.SubmissionsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
import judgels.jerahmeel.submission.JerahmeelSubmissionStore;
import judgels.jerahmeel.submission.SubmissionRoleChecker;
import judgels.jerahmeel.submission.SubmissionUtils;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;
import judgels.sandalphon.api.submission.programming.SubmissionWithSource;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("/api/v2/submissions/programming")
public class SubmissionResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject @JerahmeelSubmissionStore protected SubmissionStore submissionStore;
    @Inject protected SubmissionSourceBuilder submissionSourceBuilder;
    @Inject protected SubmissionClient submissionClient;
    @Inject protected SubmissionRegrader submissionRegrader;
    @Inject protected SubmissionRoleChecker submissionRoleChecker;
    @Inject protected JophielClient jophielClient;
    @Inject protected SandalphonClient sandalphonClient;

    @Inject protected ProblemSetStore problemSetStore;
    @Inject protected ProblemSetProblemStore problemSetProblemStore;

    @Inject protected ChapterStore chapterStore;
    @Inject protected ChapterProblemStore chapterProblemStore;

    @Inject public SubmissionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public SubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("containerJid") Optional<String> containerJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemJid") Optional<String> problemJid,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        if (!containerJid.isPresent() || !username.isPresent()) {
            throw new IllegalArgumentException("blocking for time being as this is expensive");
        }

        boolean canManage = submissionRoleChecker.canManage(actorJid);

        Page<Submission> submissions = submissionStore.getSubmissions(
                containerJid,
                byUserJid(username),
                byProblemJid(containerJid, problemJid, problemAlias),
                pageNumber,
                PAGE_SIZE);

        var containerJids = Lists.transform(submissions.getPage(), Submission::getContainerJid);
        var userJids = Lists.transform(submissions.getPage(), Submission::getUserJid);
        var problemJids = Lists.transform(submissions.getPage(), Submission::getProblemJid);
        if (containerJid.isPresent() && SubmissionUtils.isChapter(containerJid.get())) {
            problemJids = chapterProblemStore.getProgrammingProblemJids(containerJid.get());
        }

        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids);

        SubmissionConfig config = new SubmissionConfig.Builder()
                .canManage(canManage)
                .problemJids(problemJids)
                .build();

        Map<String, String> problemAliasesMap = new HashMap<>();
        if (!containerJid.isPresent() || SubmissionUtils.isProblemSet(containerJid.get())) {
            problemAliasesMap.putAll(problemSetProblemStore.getProblemAliasesByJids(problemJids));
        }
        if (!containerJid.isPresent() || SubmissionUtils.isChapter(containerJid.get())) {
            problemAliasesMap.putAll(chapterProblemStore.getProblemAliasesByJids(problemJids));
        }

        Map<String, String> problemNamesMap = new HashMap<>();
        if (!containerJid.isPresent()) {
            problemNamesMap = sandalphonClient.getProblemNames(problemJids, Optional.empty());
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

        return new SubmissionsResponse.Builder()
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
    public Submission getSubmission(@PathParam("submissionJid") String submissionJid) {
        return checkFound(submissionStore.getSubmissionByJid(submissionJid));
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
        boolean canViewSource;

        if (SubmissionUtils.isProblemSet(containerJid)) {
            ProblemSet problemSet = checkFound(problemSetStore.getProblemSetByJid(containerJid));
            ProblemSetProblem problem = checkFound(problemSetProblemStore.getProblem(problemSet.getJid(), problemJid));
            containerPath = checkFound(problemSetStore.getProblemSetPathByJid(containerJid));
            containerName = problemSet.getName();
            problemAlias = problem.getAlias();
            canViewSource = submissionRoleChecker.canViewProblemSetSource(actorJid, userJid, problemJid);
        } else {
            Chapter chapter = checkFound(chapterStore.getChapterByJid(containerJid));
            ChapterProblem problem = checkFound(chapterProblemStore.getProblem(problemJid));
            containerPath = checkFound(chapterStore.getChapterPathByJid(containerJid));
            containerName = chapter.getName();
            problemAlias = problem.getAlias();
            canViewSource = submissionRoleChecker.canViewChapterSource(actorJid, userJid, problemJid);
        }

        ProblemInfo problem = sandalphonClient.getProblem(submission.getProblemJid());

        Profile profile = checkFound(Optional.ofNullable(jophielClient.getProfile(userJid)));

        SubmissionWithSource submissionWithSource;
        if (canViewSource) {
            SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true);
            submissionWithSource = new SubmissionWithSource.Builder()
                    .submission(submission)
                    .source(source)
                    .build();
        } else {
            submissionWithSource = new SubmissionWithSource.Builder()
                    .submission(submission)
                    .build();
        }

        return new SubmissionWithSourceResponse.Builder()
                .data(submissionWithSource)
                .profile(profile)
                .problemAlias(problemAlias)
                .problemName(SandalphonUtils.getProblemName(problem, language))
                .containerPath(containerPath)
                .containerName(containerName)
                .build();
    }

    @GET
    @Path("/{submissionJid}/image")
    @Produces("image/png")
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceImage(@PathParam("submissionJid") String submissionJid) {
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildLightImageResponseFromText(source, Date.from(submission.getTime()));
    }

    @GET
    @Path("/{submissionJid}/image/dark")
    @Produces("image/png")
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceDarkImage(@PathParam("submissionJid") String submissionJid) {
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildDarkImageResponseFromText(source, Date.from(submission.getTime()));
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
        ProblemSubmissionConfig config = sandalphonClient.getProgrammingProblemSubmissionConfig(data.getProblemJid());
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

        ProblemSubmissionConfig config = sandalphonClient.getProgrammingProblemSubmissionConfig(submission.getProblemJid());
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
            configsMap.putAll(sandalphonClient.getProgrammingProblemSubmissionConfigs(
                    Sets.difference(Set.copyOf(problemJids), configsMap.keySet())));

            submissionRegrader.regradeSubmissions(submissions, configsMap);
        }
    }

    private Optional<String> byUserJid(Optional<String> username) {
        return username.map(u -> jophielClient.translateUsernameToJid(u).orElse(""));
    }

    private Optional<String> byProblemJid(
            Optional<String> containerJid,
            Optional<String> problemJid,
            Optional<String> problemAlias) {
        if (containerJid.isPresent() && problemAlias.isPresent()) {
            if (SubmissionUtils.isProblemSet(containerJid.get())) {
                return Optional.of(problemSetProblemStore
                        .getProblemByAlias(containerJid.get(), problemAlias.get())
                        .map(ProblemSetProblem::getProblemJid)
                        .orElse(""));
            }
            if (SubmissionUtils.isChapter(containerJid.get())) {
                return Optional.of(chapterProblemStore
                        .getProblemByAlias(containerJid.get(), problemAlias.get())
                        .map(ChapterProblem::getProblemJid)
                        .orElse(""));
            }
        }
        return problemJid;
    }
}
