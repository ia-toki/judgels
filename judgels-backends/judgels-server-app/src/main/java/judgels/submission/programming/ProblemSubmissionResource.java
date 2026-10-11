package judgels.submission.programming;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemType;
import judgels.api.catalog.problem.programming.ProblemSubmissionConfig;
import judgels.api.submission.programming.ProblemSubmissionWithSourceResponse;
import judgels.api.submission.programming.ProblemSubmissionsResponse;
import judgels.api.submission.programming.Submission;
import judgels.api.submission.programming.SubmissionData;
import judgels.api.submission.programming.SubmissionWithSource;
import judgels.catalog.problem.ProblemRoleChecker;
import judgels.catalog.problem.ProblemStore;
import judgels.catalog.problem.programming.ProgrammingProblemStore;
import judgels.core.api.AuthHeader;
import judgels.grading.api.SubmissionSource;
import judgels.persistence.api.Page;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

/** The test submissions of a programming problem. */
@Path("/api/v4/problems/{problemJid}/submissions/programming")
public class ProblemSubmissionResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProgrammingProblemStore programmingProblemStore;
    @Inject protected SubmissionStore submissionStore;
    @Inject protected SubmissionSourceBuilder submissionSourceBuilder;
    @Inject protected SubmissionClient submissionClient;
    @Inject protected SubmissionRegrader submissionRegrader;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemSubmissionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSubmissionsResponse getSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundProgramming(problemJid);
        checkAllowed(roleChecker.canView(actorJid, problem));

        Page<Submission> submissions = getProblemSubmissions(problemJid, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(submissions.getPage(), Submission::getUserJid);

        return new ProblemSubmissionsResponse.Builder()
                .data(submissions)
                .profilesMap(profileStore.getProfiles(userJids))
                .build();
    }

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSubmissionConfig getSubmissionConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundProgramming(problemJid);
        checkAllowed(roleChecker.canView(actorJid, problem));

        return programmingProblemStore.getProgrammingProblemSubmissionConfig(problemJid);
    }

    @GET
    @Path("/id/{submissionId}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemSubmissionWithSourceResponse getSubmissionWithSourceById(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("submissionId") long submissionId) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundProgramming(problemJid);
        checkAllowed(roleChecker.canView(actorJid, problem));

        Submission submission = checkFoundInProblem(submissionStore.getSubmissionById(submissionId), problemJid);
        SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());

        return new ProblemSubmissionWithSourceResponse.Builder()
                .data(new SubmissionWithSource.Builder()
                        .submission(submission)
                        .source(source)
                        .build())
                .profile(checkFound(Optional.ofNullable(profileStore.getProfile(submission.getUserJid()))))
                .build();
    }

    @POST
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Submission createSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            FormDataMultiPart parts) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundProgramming(problemJid);
        checkAllowed(roleChecker.canSubmit(actorJid, problem));

        String gradingLanguage = checkNotNull(parts.getField("gradingLanguage"), "gradingLanguage").getValue();

        // A test submission is made outside any host, so its container is the problem itself.
        SubmissionData data = new SubmissionData.Builder()
                .problemJid(problemJid)
                .containerJid(problemJid)
                .gradingLanguage(gradingLanguage)
                .build();
        SubmissionSource source = submissionSourceBuilder.fromNewSubmission(parts);
        ProblemSubmissionConfig config = programmingProblemStore.getProgrammingProblemSubmissionConfig(problemJid);
        Submission submission = submissionClient.submit(data, source, config);

        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);

        return submission;
    }

    @POST
    @Path("/{submissionJid}/regrade")
    @UnitOfWork
    public void regradeSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("submissionJid") String submissionJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundProgramming(problemJid);
        checkAllowed(roleChecker.canEdit(actorJid, problem));

        Submission submission = checkFoundInProblem(submissionStore.getSubmissionByJid(submissionJid), problemJid);
        ProblemSubmissionConfig config = programmingProblemStore.getProgrammingProblemSubmissionConfig(problemJid);

        submissionRegrader.regradeSubmission(submission, config);
    }

    @POST
    @Path("/regrade")
    @UnitOfWork(transactional = false)
    public void regradeSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundProgramming(problemJid);
        checkAllowed(roleChecker.canEdit(actorJid, problem));

        ProblemSubmissionConfig config = programmingProblemStore.getProgrammingProblemSubmissionConfig(problemJid);

        for (int pageNumber = 1;; pageNumber++) {
            List<Submission> submissions = getProblemSubmissions(problemJid, pageNumber, 100).getPage();
            if (submissions.isEmpty()) {
                break;
            }
            submissionRegrader.regradeSubmissions(submissions, Map.of(problemJid, config));
        }
    }

    private Page<Submission> getProblemSubmissions(String problemJid, int pageNumber, int pageSize) {
        return submissionStore.getSubmissions(
                Optional.empty(),
                Optional.empty(),
                Optional.of(problemJid),
                pageNumber,
                pageSize);
    }

    // Only a programming problem takes source code; a bundle problem takes a bundle submission.
    private Problem checkFoundProgramming(String problemJid) {
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        if (problem.getType() != ProblemType.PROGRAMMING) {
            throw new NotFoundException();
        }
        return problem;
    }

    private static Submission checkFoundInProblem(Optional<Submission> submission, String problemJid) {
        return checkFound(submission.filter(s -> s.getProblemJid().equals(problemJid)));
    }
}
