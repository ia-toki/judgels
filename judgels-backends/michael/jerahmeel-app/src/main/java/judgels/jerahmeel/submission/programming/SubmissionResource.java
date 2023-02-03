package judgels.jerahmeel.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.buildDarkImageResponseFromText;
import static judgels.service.ServiceUtils.buildLightImageResponseFromText;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import judgels.gabriel.api.SubmissionSource;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import judgels.jerahmeel.api.submission.programming.SubmissionService;
import judgels.jerahmeel.api.submission.programming.SubmissionsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
import judgels.jerahmeel.submission.SubmissionRoleChecker;
import judgels.jerahmeel.submission.SubmissionUtils;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;
import judgels.sandalphon.api.submission.programming.SubmissionWithSource;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.sandalphon.problem.ProblemClient;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class SubmissionResource implements SubmissionService {
    private final ActorChecker actorChecker;
    private final SubmissionStore submissionStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final SubmissionClient submissionClient;
    private final SubmissionRegrader submissionRegrader;
    private final SubmissionRoleChecker submissionRoleChecker;
    private final UserClient userClient;
    private final ProblemClient problemClient;

    private final ProblemSetStore problemSetStore;
    private final ProblemSetProblemStore problemSetProblemStore;

    private final ChapterStore chapterStore;
    private final ChapterProblemStore chapterProblemStore;

    @Inject
    public SubmissionResource(
            ActorChecker actorChecker,
            SubmissionStore submissionStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            SubmissionClient submissionClient,
            SubmissionRegrader submissionRegrader,
            SubmissionRoleChecker submissionRoleChecker,
            UserClient userClient,
            ProblemClient problemClient,

            ProblemSetStore problemSetStore,
            ProblemSetProblemStore problemSetProblemStore,

            ChapterStore chapterStore,
            ChapterProblemStore chapterProblemStore) {

        this.actorChecker = actorChecker;
        this.submissionStore = submissionStore;
        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionClient = submissionClient;
        this.submissionRegrader = submissionRegrader;
        this.submissionRoleChecker = submissionRoleChecker;
        this.userClient = userClient;
        this.problemClient = problemClient;

        this.problemSetStore = problemSetStore;
        this.problemSetProblemStore = problemSetProblemStore;

        this.chapterStore = chapterStore;
        this.chapterProblemStore = chapterProblemStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionsResponse getSubmissions(
            Optional<AuthHeader> authHeader,
            Optional<String> containerJid,
            Optional<String> username,
            Optional<String> problemJid,
            Optional<String> problemAlias,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);

        Page<Submission> submissions = submissionStore.getSubmissions(
                containerJid,
                byUserJid(username),
                byProblemJid(containerJid, problemJid, problemAlias),
                page);
        Set<String> containerJids = submissions.getPage().stream().map(Submission::getContainerJid).collect(toSet());
        Set<String> userJids = submissions.getPage().stream().map(Submission::getUserJid).collect(toSet());

        Set<String> problemJids = submissions.getPage().stream().map(Submission::getProblemJid).collect(toSet());
        if (containerJid.isPresent() && SubmissionUtils.isChapter(containerJid.get())) {
            problemJids = ImmutableSet.copyOf(chapterProblemStore.getProgrammingProblemJids(containerJid.get()));
        }

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids);

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
            problemNamesMap = problemClient.getProblemNames(problemJids, Optional.empty());
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

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionWithSourceResponse getSubmissionWithSourceById(
            Optional<AuthHeader> authHeader,
            long submissionId,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionById(submissionId));

        String containerJid = submission.getContainerJid();
        String problemJid = submission.getProblemJid();

        List<String> containerPath;
        String containerName;
        String problemAlias;
        if (SubmissionUtils.isProblemSet(containerJid)) {
            ProblemSet problemSet = checkFound(problemSetStore.getProblemSetByJid(containerJid));
            ProblemSetProblem problem = checkFound(problemSetProblemStore.getProblem(problemSet.getJid(), problemJid));
            containerPath = checkFound(problemSetStore.getProblemSetPathByJid(containerJid));
            containerName = problemSet.getName();
            problemAlias = problem.getAlias();
        } else {
            Chapter chapter = checkFound(chapterStore.getChapterByJid(containerJid));
            ChapterProblem problem = checkFound(chapterProblemStore.getProblem(problemJid));
            containerPath = checkFound(chapterStore.getChapterPathByJid(containerJid));
            containerName = chapter.getName();
            problemAlias = problem.getAlias();
        }

        ProblemInfo problem = problemClient.getProblem(submission.getProblemJid());

        String userJid = submission.getUserJid();
        Profile profile = checkFound(Optional.ofNullable(userClient.getProfile(userJid)));

        SubmissionWithSource submissionWithSource;
        if (submissionRoleChecker.canViewSource(actorJid, submission.getUserJid())) {
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

    @Override
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceImage(String submissionJid) {
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildLightImageResponseFromText(source, Date.from(submission.getTime()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceDarkImage(String submissionJid) {
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildDarkImageResponseFromText(source, Date.from(submission.getTime()));
    }

    @POST
    @Path("/")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts) {
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
        ProblemSubmissionConfig config = problemClient.getProgrammingProblemSubmissionConfig(data.getProblemJid());
        Submission submission = submissionClient.submit(data, source, config);

        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);
    }

    @Override
    @UnitOfWork
    public void regradeSubmission(AuthHeader authHeader, String submissionJid) {
        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        submissionRegrader.regradeSubmission(submission);
    }

    @Override
    @UnitOfWork
    public void regradeSubmissions(
            AuthHeader authHeader,
            Optional<String> containerJid,
            Optional<String> username,
            Optional<String> problemJid,
            Optional<String> problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        for (int page = 1;; page++) {
            List<Submission> submissions = submissionStore.getSubmissions(
                    containerJid,
                    byUserJid(username),
                    byProblemJid(containerJid, problemJid, problemAlias),
                    Optional.of(page)).getPage();

            if (submissions.isEmpty()) {
                break;
            }
            submissionRegrader.regradeSubmissions(submissions);
        }
    }

    private Optional<String> byUserJid(Optional<String> username) {
        return username.map(u -> userClient.translateUsernameToJid(u).orElse(""));
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
