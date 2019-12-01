package judgels.jerahmeel.chapter.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import judgels.gabriel.api.SubmissionSource;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.submission.programming.ChapterSubmissionService;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import judgels.jerahmeel.api.submission.programming.SubmissionsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jerahmeel.submission.SubmissionRoleChecker;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
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
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ChapterSubmissionResource implements ChapterSubmissionService {
    private final ActorChecker actorChecker;
    private final ChapterStore chapterStore;
    private final SubmissionStore submissionStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final SubmissionClient submissionClient;
    private final SubmissionRoleChecker submissionRoleChecker;
    private final ChapterProblemStore problemStore;
    private final ProfileService profileService;
    private final ProblemClient problemClient;

    @Inject
    public ChapterSubmissionResource(
            ActorChecker actorChecker,
            ChapterStore chapterStore,
            SubmissionStore submissionStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            SubmissionClient submissionClient,
            SubmissionRoleChecker submissionRoleChecker,
            ChapterProblemStore problemStore,
            ProfileService profileService,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.chapterStore = chapterStore;
        this.submissionStore = submissionStore;
        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionClient = submissionClient;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemStore = problemStore;
        this.profileService = profileService;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionsResponse getSubmissions(
            Optional<AuthHeader> authHeader,
            String chapterJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Chapter chapter = checkFound(chapterStore.getChapterByJid(chapterJid));

        boolean canManage = submissionRoleChecker.canManage(actorJid);
        Optional<String> actualUserJid = canManage ? userJid : Optional.of(actorJid);

        Page<Submission> submissions =
                submissionStore.getSubmissions(chapter.getJid(), actualUserJid, problemJid, page);

        Set<String> problemJids = submissions.getPage().stream()
                .map(Submission::getProblemJid)
                .collect(Collectors.toSet());

        Set<String> userJids = submissions.getPage().stream().map(Submission::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids);

        SubmissionConfig config = new SubmissionConfig.Builder()
                .canManage(canManage)
                .build();

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(chapter.getJid(), problemJids);

        return new SubmissionsResponse.Builder()
                .data(submissions)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionWithSourceResponse getSubmissionWithSourceById(
            AuthHeader authHeader,
            long submissionId,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionById(submissionId));
        Chapter chapter = checkFound(chapterStore.getChapterByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canView(actorJid, submission.getUserJid()));

        ChapterProblem chapterProblem =
                checkFound(problemStore.getProblem(chapter.getJid(), submission.getProblemJid()));
        ProblemInfo problem = problemClient.getProblem(chapterProblem.getProblemJid());

        String userJid = submission.getUserJid();
        Profile profile = checkFound(Optional.ofNullable(profileService.getProfile(userJid)));

        SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());
        SubmissionWithSource submissionWithSource = new SubmissionWithSource.Builder()
                .submission(submission)
                .source(source)
                .build();

        return new SubmissionWithSourceResponse.Builder()
                .data(submissionWithSource)
                .profile(profile)
                .problemAlias(chapterProblem.getAlias())
                .problemName(SandalphonUtils.getProblemName(problem, language))
                .containerName(chapter.getName())
                .build();
    }

    @POST
    @Path("/")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts) {
        String actorJid = actorChecker.check(authHeader);
        String chapterJid = checkNotNull(parts.getField("chapterJid"), "chapterJid").getValue();
        String problemJid = checkNotNull(parts.getField("problemJid"), "problemJid").getValue();
        String gradingLanguage = checkNotNull(parts.getField("gradingLanguage"), "gradingLanguage").getValue();

        checkFound(chapterStore.getChapterByJid(chapterJid));
        checkFound(problemStore.getProblem(chapterJid, problemJid));

        SubmissionData data = new SubmissionData.Builder()
                .problemJid(problemJid)
                .containerJid(chapterJid)
                .gradingLanguage(gradingLanguage)
                .build();
        SubmissionSource source = submissionSourceBuilder.fromNewSubmission(parts);
        ProblemSubmissionConfig config = problemClient.getProgrammingProblemSubmissionConfig(data.getProblemJid());
        Submission submission = submissionClient.submit(data, source, config);

        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);
    }
}
