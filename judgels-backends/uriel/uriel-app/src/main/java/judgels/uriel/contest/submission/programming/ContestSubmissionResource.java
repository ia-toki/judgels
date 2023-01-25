package judgels.uriel.contest.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.buildDarkImageResponseFromText;
import static judgels.service.ServiceUtils.buildLightImageResponseFromText;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;
import judgels.sandalphon.api.submission.programming.SubmissionInfo;
import judgels.sandalphon.api.submission.programming.SubmissionWithSource;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.sandalphon.problem.ProblemClient;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionDownloader;
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionService;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalMarker;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ContestSubmissionResource implements ContestSubmissionService {
    private static final String LAST_SUBMISSION_ID_HEADER = "Last-Submission-Id";

    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final SubmissionStore submissionStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final SubmissionDownloader submissionDownloader;
    private final SubmissionClient submissionClient;
    private final SubmissionRegrader submissionRegrader;
    private final ScoreboardIncrementalMarker scoreboardIncrementalMarker;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestModuleStore moduleStore;
    private final ContestContestantStore contestantStore;
    private final ContestSupervisorStore supervisorStore;
    private final ContestProblemStore problemStore;
    private final UserClient userClient;
    private final ProblemClient problemClient;

    @Inject
    public ContestSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            SubmissionStore submissionStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            SubmissionDownloader submissionDownloader,
            SubmissionClient submissionClient,
            SubmissionRegrader submissionRegrader,
            ScoreboardIncrementalMarker scoreboardIncrementalMarker,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestSupervisorStore supervisorStore,
            ContestProblemStore problemStore,
            UserClient userClient,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.submissionStore = submissionStore;
        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionDownloader = submissionDownloader;
        this.submissionClient = submissionClient;
        this.submissionRegrader = submissionRegrader;
        this.submissionRoleChecker = submissionRoleChecker;
        this.scoreboardIncrementalMarker = scoreboardIncrementalMarker;
        this.problemRoleChecker = problemRoleChecker;
        this.moduleStore = moduleStore;
        this.contestantStore = contestantStore;
        this.supervisorStore = supervisorStore;
        this.problemStore = problemStore;
        this.userClient = userClient;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestSubmissionsResponse getSubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            Optional<String> problemAlias,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);

        Page<Submission> submissions = submissionStore.getSubmissions(
                Optional.of(contest.getJid()),
                canSupervise ? byUserJid(username) : Optional.of(actorJid),
                byProblemJid(contestJid, problemAlias),
                page);

        List<String> userJidsSortedByUsername;
        Set<String> userJids;

        List<String> problemJidsSortedByAlias;
        Set<String> problemJids;

        userJids = submissions.getPage().stream().map(Submission::getUserJid).collect(Collectors.toSet());
        if (canSupervise) {
            userJids.addAll(contestantStore.getApprovedContestantJids(contestJid));
            userJids.addAll(supervisorStore.getAllSupervisorJids(contestJid));
            userJidsSortedByUsername = Lists.newArrayList(userJids);

            problemJidsSortedByAlias = problemStore.getProblemJids(contestJid);
            problemJids = ImmutableSet.copyOf(problemJidsSortedByAlias);
        } else {
            userJidsSortedByUsername = Collections.emptyList();

            problemJidsSortedByAlias = Collections.emptyList();
            problemJids = submissions.getPage().stream()
                    .map(Submission::getProblemJid)
                    .collect(Collectors.toSet());
        }

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

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

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        return new ContestSubmissionsResponse.Builder()
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
        Contest contest = checkFound(contestStore.getContestByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canView(actorJid, contest, submission.getUserJid()));

        ContestProblem contestProblem =
                checkFound(problemStore.getProblem(contest.getJid(), submission.getProblemJid()));
        ProblemInfo problem = problemClient.getProblem(contestProblem.getProblemJid());

        String userJid = submission.getUserJid();
        Profile profile = checkFound(Optional.ofNullable(userClient.getProfile(userJid, contest.getBeginTime())));

        SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true);
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

    @Override
    @UnitOfWork(readOnly = true)
    public SubmissionInfo getSubmissionInfo(String contestJid, String userJid, String problemJid) {
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewAll(contest));

        Submission submission = checkFound(submissionStore
                .getLatestSubmission(Optional.of(contestJid), Optional.of(userJid), Optional.of(problemJid)));
        Profile profile = this.userClient.getProfile(userJid);

        return new SubmissionInfo.Builder().id(submission.getId()).profile(profile).build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceImage(String contestJid, String userJid, String problemJid) {
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewAll(contest));

        Submission submission = checkFound(submissionStore
                .getLatestSubmission(Optional.of(contestJid), Optional.of(userJid), Optional.of(problemJid)));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildLightImageResponseFromText(source, Date.from(submission.getTime()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceDarkImage(String contestJid, String userJid, String problemJid) {
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewAll(contest));

        Submission submission = checkFound(submissionStore
                .getLatestSubmission(Optional.of(contestJid), Optional.of(userJid), Optional.of(problemJid)));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildDarkImageResponseFromText(source, Date.from(submission.getTime()));
    }

    @POST
    @Path("/")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void createSubmission(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, FormDataMultiPart parts) {
        String actorJid = actorChecker.check(authHeader);
        String contestJid = checkNotNull(parts.getField("contestJid"), "contestJid").getValue();
        String problemJid = checkNotNull(parts.getField("problemJid"), "problemJid").getValue();
        String gradingLanguage = checkNotNull(parts.getField("gradingLanguage"), "gradingLanguage").getValue();

        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        ContestProblem problem = checkFound(problemStore.getProblem(contestJid, problemJid));
        long totalSubmissions = submissionStore.getTotalSubmissions(contestJid, actorJid, problemJid);
        checkAllowed(problemRoleChecker.canSubmit(actorJid, contest, problem, totalSubmissions));

        StyleModuleConfig styleConfig = moduleStore.getStyleModuleConfig(contestJid, contest.getStyle());
        LanguageRestriction contestGradingLanguageRestriction = styleConfig.getGradingLanguageRestriction();

        SubmissionData data = new SubmissionData.Builder()
                .problemJid(problemJid)
                .containerJid(contestJid)
                .gradingLanguage(gradingLanguage)
                .additionalGradingLanguageRestriction(contestGradingLanguageRestriction)
                .build();
        SubmissionSource source = submissionSourceBuilder.fromNewSubmission(parts);
        ProblemSubmissionConfig config = problemClient.getProgrammingProblemSubmissionConfig(data.getProblemJid());
        Submission submission = submissionClient.submit(data, source, config);

        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);

        contestLogger.log(contestJid, "SUBMIT", submission.getJid(), problemJid);
    }

    @Override
    @UnitOfWork
    public void regradeSubmission(AuthHeader authHeader, String submissionJid) {
        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        Contest contest = checkFound(contestStore.getContestByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canManage(actorJid, contest));

        submissionRegrader.regradeSubmission(submission);
        scoreboardIncrementalMarker.invalidateMark(contest.getJid());

        contestLogger.log(contest.getJid(), "REGRADE_SUBMISSION", submissionJid, submission.getProblemJid());
    }

    @Override
    @UnitOfWork
    public void regradeSubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            Optional<String> problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canManage(actorJid, contest));

        Optional<String> problemJid = byProblemJid(contestJid, problemAlias);

        for (int page = 1;; page++) {
            List<Submission> submissions = submissionStore.getSubmissions(
                    Optional.of(contestJid),
                    byUserJid(username),
                    problemJid,
                    Optional.of(page)).getPage();

            if (submissions.isEmpty()) {
                break;
            }
            submissionRegrader.regradeSubmissions(submissions);
        }
        scoreboardIncrementalMarker.invalidateMark(contest.getJid());

        contestLogger.log(contest.getJid(), "REGRADE_SUBMISSIONS", null, problemJid.orElse(null));
    }

    @GET
    @Path("/{submissionJid}/download")
    @UnitOfWork(readOnly = true)
    @Produces(APPLICATION_OCTET_STREAM)
    public Response downloadSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionJid") String submissionJid) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        Contest contest = checkFound(contestStore.getContestByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canView(actorJid, contest, submission.getUserJid()));

        StreamingOutput stream = output -> submissionDownloader.downloadAsZip(output, submission);
        return Response.ok(stream)
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .header("Content-Disposition", ContentDisposition
                        .type("attachment")
                        .fileName(submission.getId() + ".zip")
                        .build())
                .build();
    }

    @GET
    @Path("/download")
    @UnitOfWork(readOnly = true)
    @Produces(APPLICATION_OCTET_STREAM)
    public Response downloadSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") Optional<String> userJid,
            @QueryParam("problemJid") Optional<String> problemJid,
            @QueryParam("lastSubmissionId") Optional<Long> lastSubmissionId,
            @QueryParam("limit") Optional<Integer> limit) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canSupervise(actorJid, contest));

        List<Submission> submissions = submissionStore
                .getSubmissionsForDownload(Optional.of(contestJid), userJid, problemJid, lastSubmissionId, limit)
                .getPage();

        if (submissions.isEmpty()) {
            return Response.noContent().build();
        }

        Set<String> userJids = contestantStore.getApprovedContestantJids(contestJid);
        Map<String, String> usernamesMap = userClient.getProfiles(userJids).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getUsername()));

        Set<String> problemJids = submissions.stream()
                .map(Submission::getProblemJid)
                .collect(Collectors.toSet());
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contestJid, problemJids);

        StreamingOutput stream =
                output -> submissionDownloader.downloadAsZip(output, submissions, usernamesMap, problemAliasesMap);

        return Response.ok(stream)
                .header(LAST_SUBMISSION_ID_HEADER, submissions.get(submissions.size() - 1).getId())
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public List<Submission> getSubmissionsForStats(
            String contestJid,
            Optional<Long> lastSubmissionId,
            Optional<Integer> limit) {


        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewAll(contest));

        return submissionStore
                .getSubmissionsForStats(Optional.of(contestJid), lastSubmissionId, limit.orElse(100))
                .getPage();
    }

    private Optional<String> byUserJid(Optional<String> username) {
        return username.map(u -> userClient.translateUsernameToJid(u).orElse(""));
    }

    private Optional<String> byProblemJid(
            String contestJid,
            Optional<String> problemAlias) {
        return problemAlias.map(alias -> problemStore
                .getProblemByAlias(contestJid, alias)
                .map(ContestProblem::getProblemJid)
                .orElse(""));
    }
}
