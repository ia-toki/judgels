package judgels.contest.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.buildDarkImageResponseFromText;
import static judgels.service.ServiceUtils.buildLightImageResponseFromText;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
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
import jakarta.ws.rs.core.StreamingOutput;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.api.contest.Contest;
import judgels.api.contest.module.StyleModuleConfig;
import judgels.api.contest.problem.ContestProblem;
import judgels.api.contest.submission.ContestSubmissionConfig;
import judgels.api.contest.submission.programming.ContestSubmissionsResponse;
import judgels.api.contest.submission.programming.ContestUserProblemSubmissionsResponse;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.programming.ProblemSubmissionConfig;
import judgels.api.profile.Profile;
import judgels.api.submission.programming.Submission;
import judgels.api.submission.programming.SubmissionData;
import judgels.api.submission.programming.SubmissionInfo;
import judgels.api.submission.programming.SubmissionWithSource;
import judgels.api.submission.programming.SubmissionWithSourceResponse;
import judgels.contest.ContestStore;
import judgels.contest.contestant.ContestContestantStore;
import judgels.contest.log.ContestLogger;
import judgels.contest.module.ContestModuleStore;
import judgels.contest.problem.ContestProblemRoleChecker;
import judgels.contest.problem.ContestProblemStore;
import judgels.contest.scoreboard.ScoreboardIncrementalMarker;
import judgels.contest.submission.ContestSubmissionRoleChecker;
import judgels.contest.supervisor.ContestSupervisorStore;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.persistence.api.Page;
import judgels.problem.ProblemService;
import judgels.profile.ProfileStore;
import judgels.sandalphon.SandalphonUtils;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.submission.programming.SubmissionClient;
import judgels.submission.programming.SubmissionDownloader;
import judgels.submission.programming.SubmissionRegrader;
import judgels.submission.programming.SubmissionSourceBuilder;
import judgels.submission.programming.SubmissionStore;
import judgels.user.UserStore;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("/api/v2/contests/submissions/programming")
public class ContestSubmissionResource {
    private static final int MAX_DOWNLOAD_SUBMISSIONS_LIMIT = 5000;
    private static final String LAST_SUBMISSION_ID_HEADER = "Last-Submission-Id";
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected SubmissionStore submissionStore;
    @Inject protected SubmissionSourceBuilder submissionSourceBuilder;
    @Inject protected SubmissionDownloader submissionDownloader;
    @Inject protected SubmissionClient submissionClient;
    @Inject protected SubmissionRegrader submissionRegrader;
    @Inject protected ScoreboardIncrementalMarker scoreboardIncrementalMarker;
    @Inject protected ContestSubmissionRoleChecker submissionRoleChecker;
    @Inject protected ContestProblemRoleChecker problemRoleChecker;
    @Inject protected ContestModuleStore moduleStore;
    @Inject protected ContestContestantStore contestantStore;
    @Inject protected ContestSupervisorStore supervisorStore;
    @Inject protected ContestProblemStore problemStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;
    @Inject protected ProblemService problemService;

    @Inject public ContestSubmissionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestSubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = submissionRoleChecker.canSupervise(actorJid, contest);

        Page<Submission> submissions = submissionStore.getSubmissions(
                Optional.of(contest.getJid()),
                canSupervise ? byUserJid(username) : Optional.of(actorJid),
                byProblemJid(contestJid, problemAlias),
                pageNumber,
                PAGE_SIZE);

        List<String> userJidsSortedByUsername;
        Set<String> userJids;

        List<String> problemJidsSortedByAlias;
        Set<String> problemJids;

        userJids = submissions.getPage().stream().map(Submission::getUserJid).collect(toSet());
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
                    .collect(toSet());
        }

        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids, contest.getBeginTime());

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

    @GET
    @Path("/user-problem")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestUserProblemSubmissionsResponse getUserProblemSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") String userJid,
            @QueryParam("problemJid") String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canSupervise(actorJid, contest));

        List<Submission> submissions = submissionStore.getUserProblemSubmissions(
                contest.getJid(),
                userJid,
                problemJid);

        Optional<SubmissionSource> latestSubmissionSource = submissions.isEmpty()
                ? Optional.empty()
                : Optional.of(submissionSourceBuilder.fromPastSubmission(submissions.get(0).getJid(), true));

        return new ContestUserProblemSubmissionsResponse.Builder()
                .data(submissions)
                .latestSubmissionSource(latestSubmissionSource)
                .build();
    }

    @GET
    @Path("/id/{submissionId}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public SubmissionWithSourceResponse getSubmissionWithSourceById(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionId") long submissionId,
            @QueryParam("language") Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionById(submissionId));
        Contest contest = checkFound(contestStore.getContestByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canView(actorJid, contest, submission.getUserJid()));

        ContestProblem contestProblem =
                checkFound(problemStore.getProblem(contest.getJid(), submission.getProblemJid()));
        ProblemInfo problem = problemService.getProblem(contestProblem.getProblemJid());

        String userJid = submission.getUserJid();
        Profile profile = checkFound(Optional.ofNullable(profileStore.getProfile(userJid, contest.getBeginTime())));

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

    @GET
    @Path("/info")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public SubmissionInfo getSubmissionInfo(
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") String userJid,
            @QueryParam("problemJid") String problemJid) {

        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewAll(contest));

        Submission submission = checkFound(submissionStore
                .getLatestSubmission(Optional.of(contestJid), Optional.of(userJid), Optional.of(problemJid)));
        Profile profile = this.profileStore.getProfile(userJid);

        return new SubmissionInfo.Builder().id(submission.getId()).profile(profile).build();
    }

    @GET
    @Path("/image")
    @Produces("image/png")
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceImage(
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") String userJid,
            @QueryParam("problemJid") String problemJid) {

        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewAll(contest));

        Submission submission = checkFound(submissionStore
                .getLatestSubmission(Optional.of(contestJid), Optional.of(userJid), Optional.of(problemJid)));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildLightImageResponseFromText(source, Date.from(submission.getTime()));
    }

    @GET
    @Path("/image/dark")
    @Produces("image/png")
    @UnitOfWork(readOnly = true)
    public Response getSubmissionSourceDarkImage(
            @QueryParam("contestJid") String contestJid,
            @QueryParam("userJid") String userJid,
            @QueryParam("problemJid") String problemJid) {

        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewAll(contest));

        Submission submission = checkFound(submissionStore
                .getLatestSubmission(Optional.of(contestJid), Optional.of(userJid), Optional.of(problemJid)));
        String source = submissionSourceBuilder.fromPastSubmission(submission.getJid(), true).asString();

        return buildDarkImageResponseFromText(source, Date.from(submission.getTime()));
    }

    @POST
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void createSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            FormDataMultiPart parts) {

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
        ProblemSubmissionConfig config = problemService.getProgrammingProblemSubmissionConfig(data.getProblemJid());
        Submission submission = submissionClient.submit(data, source, config);

        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);

        contestLogger.log(contestJid, "SUBMIT", submission.getJid(), problemJid);
    }

    @POST
    @Path("/{submissionJid}/regrade")
    @UnitOfWork
    public void regradeSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("submissionJid") String submissionJid) {

        String actorJid = actorChecker.check(authHeader);
        Submission submission = checkFound(submissionStore.getSubmissionByJid(submissionJid));
        Contest contest = checkFound(contestStore.getContestByJid(submission.getContainerJid()));
        checkAllowed(submissionRoleChecker.canManage(actorJid, contest));

        ProblemSubmissionConfig config = problemService.getProgrammingProblemSubmissionConfig(submission.getProblemJid());
        submissionRegrader.regradeSubmission(submission, config);

        scoreboardIncrementalMarker.invalidateMark(contest.getJid());

        contestLogger.log(contest.getJid(), "REGRADE_SUBMISSION", submissionJid, submission.getProblemJid());
    }

    @POST
    @Path("/regrade")
    @UnitOfWork(transactional = false)
    public void regradeSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("contestJid") String contestJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canManage(actorJid, contest));

        Optional<String> problemJid = byProblemJid(contestJid, problemAlias);

        Map<String, ProblemSubmissionConfig> configsMap = new HashMap<>();

        for (int pageNumber = 1;; pageNumber++) {
            List<Submission> submissions = submissionStore.getSubmissions(
                    Optional.of(contestJid),
                    byUserJid(username),
                    problemJid,
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

        int pageSize = Math.min(MAX_DOWNLOAD_SUBMISSIONS_LIMIT, limit.orElse(MAX_DOWNLOAD_SUBMISSIONS_LIMIT));
        List<Submission> submissions = submissionStore
                .getSubmissionsForDownload(Optional.of(contestJid), userJid, problemJid, lastSubmissionId, pageSize)
                .getPage();

        if (submissions.isEmpty()) {
            return Response.noContent().build();
        }

        var userJids = contestantStore.getApprovedContestantJids(contestJid);
        Map<String, String> usernamesMap = profileStore.getProfiles(userJids).entrySet()
                .stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue().getUsername()));

        var problemJids = Lists.transform(submissions, Submission::getProblemJid);
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contestJid, problemJids);

        StreamingOutput stream =
                output -> submissionDownloader.downloadAsZip(output, submissions, usernamesMap, problemAliasesMap);

        return Response.ok(stream)
                .header(LAST_SUBMISSION_ID_HEADER, submissions.get(submissions.size() - 1).getId())
                .build();
    }

    private Optional<String> byUserJid(Optional<String> username) {
        return username.map(u -> userStore.translateUsernameToJid(u).orElse(""));
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
