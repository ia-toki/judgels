package judgels.uriel.contest.submission;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.jophiel.api.info.Profile;
import judgels.jophiel.api.info.ProfileService;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.Submission;
import judgels.sandalphon.api.submission.SubmissionWithSource;
import judgels.sandalphon.api.submission.SubmissionWithSourceResponse;
import judgels.sandalphon.submission.SubmissionData;
import judgels.sandalphon.submission.SubmissionSourceBuilder;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.ContestSubmissionService;
import judgels.uriel.api.contest.submission.ContestSubmissionsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.style.ContestStyleConfig;
import judgels.uriel.contest.style.ContestStyleStore;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ContestSubmissionResource implements ContestSubmissionService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestStyleStore styleStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final ContestSubmissionClient submissionClient;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestSubmissionStore submissionStore;
    private final ContestProblemStore problemStore;
    private final ProfileService profileService;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestStyleStore styleStore,
            ContestProblemStore problemStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            ContestSubmissionClient submissionClient,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestSubmissionStore submissionStore,
            ProfileService profileService,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.styleStore = styleStore;
        this.problemStore = problemStore;
        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionClient = submissionClient;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.submissionStore = submissionStore;
        this.profileService = profileService;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestSubmissionsResponse getMySubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwnSubmissions(actorJid, contest));

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        Page<Submission> data = submissionStore.getSubmissions(contestJid, actorJid, options.build());
        Set<String> userJids = data.getData().stream().map(Submission::getUserJid).collect(Collectors.toSet());
        Set<String> problemJids = data.getData().stream().map(Submission::getProblemJid).collect(Collectors.toSet());

        return new ContestSubmissionsResponse.Builder()
                .data(data)
                .profilesMap(profileService.getPastProfiles(userJids))
                .problemAliasesMap(problemStore.getProblemAliasesByJids(contestJid, problemJids))
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
        Contest contest = checkFound(contestStore.getContestByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canViewSubmission(actorJid, contest, submission.getUserJid()));

        ContestProblem contestProblem =
                checkFound(problemStore.getProblem(contest.getJid(), submission.getProblemJid()));
        ProblemInfo problem =
                clientProblemService.getProblem(sandalphonClientAuthHeader, contestProblem.getProblemJid());

        String userJid = submission.getUserJid();
        Profile profile = checkFound(Optional.ofNullable(
                profileService.getPastProfiles(ImmutableSet.of(userJid)).get(userJid)));

        SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());
        SubmissionWithSource submissionWithSource = new SubmissionWithSource.Builder()
                .submission(submission)
                .source(source)
                .build();

        return new SubmissionWithSourceResponse.Builder()
                .data(submissionWithSource)
                .profile(profile)
                .problemAlias(contestProblem.getAlias())
                .problemName(SandalphonUtils.getProblemName(problem, language))
                .containerName(contest.getName())
                .build();
    }

    @POST
    @Path("/submissions")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts) {
        String actorJid = actorChecker.check(authHeader);
        String contestJid = checkNotNull(parts.getField("contestJid"), "contestJid").getValue();
        String problemJid = checkNotNull(parts.getField("problemJid"), "problemJid").getValue();
        String gradingLanguage = checkNotNull(parts.getField("gradingLanguage"), "gradingLanguage").getValue();

        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        ContestContestantProblem contestantProblem =
                checkFound(problemStore.getContestantProblem(contestJid, actorJid, problemJid));
        checkAllowed(problemRoleChecker.canSubmitProblem(actorJid, contest, contestantProblem));

        ContestStyleConfig styleConfig = styleStore.getStyleConfig(contestJid);
        LanguageRestriction contestGradingLanguageRestriction = styleConfig.getGradingLanguageRestriction();

        SubmissionData data = new SubmissionData.Builder()
                .userJid(actorJid)
                .problemJid(problemJid)
                .containerJid(contestJid)
                .gradingLanguage(gradingLanguage)
                .additionalGradingLanguageRestriction(contestGradingLanguageRestriction)
                .build();
        SubmissionSource source = submissionSourceBuilder.fromNewSubmission(parts);
        ProblemSubmissionConfig config =
                clientProblemService.getProblemSubmissionConfig(sandalphonClientAuthHeader, data.getProblemJid());
        Submission submission = submissionClient.submit(data, source, config);

        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);
    }
}
