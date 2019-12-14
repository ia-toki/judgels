package judgels.jerahmeel.problemset.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.List;
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
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.problemset.submission.programming.ProblemSetSubmissionService;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import judgels.jerahmeel.api.submission.programming.SubmissionsResponse;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
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
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ProblemSetSubmissionResource implements ProblemSetSubmissionService {
    private final ActorChecker actorChecker;
    private final ProblemSetStore problemSetStore;
    private final SubmissionStore submissionStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final SubmissionClient submissionClient;
    private final SubmissionRegrader submissionRegrader;
    private final SubmissionRoleChecker submissionRoleChecker;
    private final ProblemSetProblemStore problemStore;
    private final ProfileService profileService;
    private final ProblemClient problemClient;

    @Inject
    public ProblemSetSubmissionResource(
            ActorChecker actorChecker,
            ProblemSetStore problemSetStore,
            SubmissionStore submissionStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            SubmissionClient submissionClient,
            SubmissionRegrader submissionRegrader,
            SubmissionRoleChecker submissionRoleChecker,
            ProblemSetProblemStore problemStore,
            ProfileService profileService,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.problemSetStore = problemSetStore;
        this.submissionStore = submissionStore;
        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionClient = submissionClient;
        this.submissionRegrader = submissionRegrader;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemStore = problemStore;
        this.profileService = profileService;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionsResponse getSubmissions(
            Optional<AuthHeader> authHeader,
            Optional<String> problemSetJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);

        boolean canManage = submissionRoleChecker.canManage(actorJid);

        Page<Submission> submissions = submissionStore.getSubmissions(problemSetJid, userJid, problemJid, page);

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

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(problemJids);

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
        ProblemSet problemSet = checkFound(problemSetStore.getProblemSetByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canView(actorJid, submission.getUserJid()));

        ProblemSetProblem problemSetProblem = checkFound(problemStore.getProblem(submission.getProblemJid()));
        ProblemInfo problem = problemClient.getProblem(problemSetProblem.getProblemJid());

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
                .problemAlias(problemSetProblem.getAlias())
                .problemName(SandalphonUtils.getProblemName(problem, language))
                .containerName(problemSet.getName())
                .build();
    }

    @POST
    @Path("/")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts) {
        String actorJid = actorChecker.check(authHeader);
        String problemSetJid = checkNotNull(parts.getField("problemSetJid"), "problemSetJid").getValue();
        String problemJid = checkNotNull(parts.getField("problemJid"), "problemJid").getValue();
        String gradingLanguage = checkNotNull(parts.getField("gradingLanguage"), "gradingLanguage").getValue();

        checkFound(problemSetStore.getProblemSetByJid(problemSetJid));
        checkFound(problemStore.getProblem(problemJid));

        SubmissionData data = new SubmissionData.Builder()
                .problemJid(problemJid)
                .containerJid(problemSetJid)
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
        checkFound(problemSetStore.getProblemSetByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        submissionRegrader.regradeSubmission(submission);
    }

    @Override
    @UnitOfWork
    public void regradeSubmissions(
            AuthHeader authHeader,
            Optional<String> problemSetJid,
            Optional<String> userJid,
            Optional<String> problemJid) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(submissionRoleChecker.canManage(actorJid));

        for (int page = 1;; page++) {
            List<Submission> submissions = submissionStore
                    .getSubmissions(problemSetJid, userJid, problemJid, Optional.of(page))
                    .getPage();

            if (submissions.isEmpty()) {
                break;
            }
            submissionRegrader.regradeSubmissions(submissions);
        }
    }
}
