package judgels.submission.bundle;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
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
import java.util.Optional;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemType;
import judgels.api.submission.bundle.BundleAnswer;
import judgels.api.submission.bundle.BundleSubmission;
import judgels.api.submission.bundle.ProblemBundleSubmissionWithAnswerResponse;
import judgels.api.submission.bundle.ProblemBundleSubmissionsResponse;
import judgels.catalog.problem.ProblemRoleChecker;
import judgels.catalog.problem.ProblemStore;
import judgels.catalog.problem.statement.ProblemStatementStore;
import judgels.core.api.AuthHeader;
import judgels.persistence.api.Page;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;

/** The test submissions of a bundle problem, each answering the whole problem at once. */
@Path("/api/v4/problems/{problemJid}/submissions/bundle")
public class ProblemBundleSubmissionResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemStatementStore statementStore;
    @Inject protected BundleSubmissionStore submissionStore;
    @Inject protected BundleSubmissionClient submissionClient;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemBundleSubmissionResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemBundleSubmissionsResponse getBundleSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundBundle(problemJid);
        checkAllowed(roleChecker.canView(actorJid, problem));

        Page<BundleSubmission> submissions = submissionStore.getSubmissions(problemJid, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(submissions.getPage(), BundleSubmission::getAuthorJid);

        return new ProblemBundleSubmissionsResponse.Builder()
                .data(submissions)
                .profilesMap(profileStore.getProfiles(userJids))
                .build();
    }

    @GET
    @Path("/id/{submissionId}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemBundleSubmissionWithAnswerResponse getBundleSubmissionWithAnswerById(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("submissionId") long submissionId) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundBundle(problemJid);
        checkAllowed(roleChecker.canView(actorJid, problem));

        BundleSubmission submission = checkFoundInProblem(submissionStore.getSubmissionById(submissionId), problemJid);

        return new ProblemBundleSubmissionWithAnswerResponse.Builder()
                .data(submission)
                .answer(submissionClient.createBundleAnswerFromPastSubmission(submission.getJid()))
                .profile(checkFound(Optional.ofNullable(profileStore.getProfile(submission.getAuthorJid()))))
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void createBundleSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            BundleAnswer data) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundBundle(problemJid);
        checkAllowed(roleChecker.canSubmit(actorJid, problem));

        if (!statementStore.getStatementEnabledLanguages(null, problemJid).contains(data.getLanguageCode())) {
            throw new BadRequestException();
        }

        // An item left empty is not answered, so it must not be graded as a wrong answer.
        BundleAnswer answer = new BundleAnswer.Builder()
                .answers(Maps.filterValues(data.getAnswers(), a -> !a.isEmpty()))
                .languageCode(data.getLanguageCode())
                .build();

        submissionClient.submit(problemJid, answer);
    }

    @POST
    @Path("/{submissionJid}/regrade")
    @UnitOfWork
    public void regradeBundleSubmission(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("submissionJid") String submissionJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundBundle(problemJid);
        checkAllowed(roleChecker.canEdit(actorJid, problem));

        submissionClient.regradeSubmission(
                checkFoundInProblem(submissionStore.getSubmissionByJid(submissionJid), problemJid));
    }

    @POST
    @Path("/regrade")
    @UnitOfWork
    public void regradeBundleSubmissions(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFoundBundle(problemJid);
        checkAllowed(roleChecker.canEdit(actorJid, problem));

        for (int pageNumber = 1;; pageNumber++) {
            List<BundleSubmission> submissions = submissionStore.getSubmissions(problemJid, pageNumber, 100).getPage();
            if (submissions.isEmpty()) {
                break;
            }
            submissionClient.regradeSubmissions(submissions);
        }
    }

    // Only a bundle problem is answered as a whole; a programming problem takes source code.
    private Problem checkFoundBundle(String problemJid) {
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        if (problem.getType() != ProblemType.BUNDLE) {
            throw new NotFoundException();
        }
        return problem;
    }

    private static BundleSubmission checkFoundInProblem(Optional<BundleSubmission> submission, String problemJid) {
        return checkFound(submission.filter(s -> s.getProblemJid().equals(problemJid)));
    }
}
