package judgels.uriel.contest.submission;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SourceFile;
import judgels.gabriel.api.SubmissionSource;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.OrderDir;
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
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.submission.ContestSubmissionService;
import judgels.uriel.api.contest.submission.ContestSubmissionsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.style.ContestStyleConfig;
import judgels.uriel.contest.style.ContestStyleStore;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ContestSubmissionResource implements ContestSubmissionService {
    private static final int MAX_SUBMISSIONS_DOWNLOAD_PAGE_SIZE = 100;
    private static final String LAST_SUBMISSION_ID_HEADER = "Last-Submission-Id";

    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestStyleStore styleStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final ContestSubmissionClient submissionClient;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestProblemRoleChecker problemRoleChecker;
    private final ContestSubmissionStore submissionStore;
    private final ContestContestantStore contestantStore;
    private final ContestProblemStore problemStore;
    private final ProfileService profileService;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestSubmissionResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestStyleStore styleStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            ContestSubmissionClient submissionClient,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestProblemRoleChecker problemRoleChecker,
            ContestSubmissionStore submissionStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            ProfileService profileService,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.styleStore = styleStore;
        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionClient = submissionClient;
        this.submissionRoleChecker = submissionRoleChecker;
        this.problemRoleChecker = problemRoleChecker;
        this.submissionStore = submissionStore;
        this.contestantStore = contestantStore;
        this.problemStore = problemStore;
        this.profileService = profileService;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestSubmissionsResponse getSubmissions(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(submissionRoleChecker.canViewOwnSubmissions(actorJid, contest));

        Optional<String> actualUserJid = submissionRoleChecker.canViewAllSubmissions(actorJid, contest)
                ? userJid
                : Optional.of(actorJid);

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        Page<Submission> data = submissionStore
                .getSubmissions(contest.getJid(), actualUserJid, problemJid, Optional.empty(), options.build());
        Set<String> userJids = data.getData().stream().map(Submission::getUserJid).collect(Collectors.toSet());
        Set<String> problemJids = data.getData().stream().map(Submission::getProblemJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? ImmutableMap.of()
                : profileService.getProfiles(userJids, contest.getBeginTime());
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        return new ContestSubmissionsResponse.Builder()
                .data(data)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestSubmissionConfig getSubmissionConfig(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        if (!submissionRoleChecker.canViewAllSubmissions(actorJid, contest)) {
            return new ContestSubmissionConfig.Builder()
                    .isAllowedToViewAllSubmissions(false)
                    .build();
        }

        Set<String> userJids = contestantStore.getContestants(contestJid);
        Map<String, String> usernamesMap = userJids.isEmpty()
                ? ImmutableMap.of()
                : profileService.getProfiles(userJids).entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getUsername()));
        List<String> problemJids = problemStore.getOpenProblemJids(contestJid);
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contestJid, problemJidsSet);
        return new ContestSubmissionConfig.Builder()
                .isAllowedToViewAllSubmissions(true)
                .usernamesMap(usernamesMap)
                .problemJids(problemJids)
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
        checkAllowed(submissionRoleChecker.canViewSubmission(actorJid, contest, submission.getUserJid()));

        ContestProblem contestProblem =
                checkFound(problemStore.getProblem(contest.getJid(), submission.getProblemJid()));
        ProblemInfo problem =
                clientProblemService.getProblem(sandalphonClientAuthHeader, contestProblem.getProblemJid());

        String userJid = submission.getUserJid();
        Profile profile = checkFound(Optional.ofNullable(
                profileService.getProfiles(ImmutableSet.of(userJid), contest.getBeginTime()).get(userJid)));

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
    @Path("/")
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

    @GET
    @Path("/download")
    @UnitOfWork
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
        checkAllowed(submissionRoleChecker.canViewAllSubmissions(actorJid, contest));

        int pageSize = Math.min(MAX_SUBMISSIONS_DOWNLOAD_PAGE_SIZE, limit.orElse(MAX_SUBMISSIONS_DOWNLOAD_PAGE_SIZE));
        SelectionOptions options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .pageSize(pageSize)
                .orderDir(OrderDir.ASC)
                .build();

        List<Submission> submissions = submissionStore
                .getSubmissions(contest.getJid(), userJid, problemJid, lastSubmissionId, options)
                .getData();

        if (submissions.isEmpty()) {
            return Response.noContent().build();
        }

        StreamingOutput stream = output -> {
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(output))) {
                for (Submission submission : submissions) {
                    String dirName = submission.getId() + "/" + submission.getJid() + "/";

                    SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());
                    int fileCount = source.getSubmissionFiles().size();
                    for (Map.Entry<String, SourceFile> entry : source.getSubmissionFiles().entrySet()) {
                        String field = entry.getKey();
                        SourceFile file = entry.getValue();

                        String filename;
                        if (fileCount > 1) {
                            filename = dirName + field + "/" + file.getName();
                        } else {
                            filename = dirName + file.getName();
                        }

                        ZipEntry ze = new ZipEntry(filename);
                        zos.putNextEntry(ze);
                        zos.write(file.getContent());
                        zos.closeEntry();
                    }
                }
            }
            output.flush();
        };

        return Response.ok(stream)
                .header(LAST_SUBMISSION_ID_HEADER, submissions.get(submissions.size() - 1).getId())
                .build();
    }
}
