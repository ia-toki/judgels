package judgels.michael.problem.bundle.submission;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.problem.bundle.BaseBundleProblemResource;
import judgels.michael.template.HtmlTemplate;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;
import judgels.sandalphon.problem.bundle.submission.BundleSubmissionClient;
import judgels.sandalphon.problem.bundle.submission.BundleSubmissionStore;

@Path("/problems/bundle/{problemId}/submissions")
public class BundleProblemSubmissionResource extends BaseBundleProblemResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected BundleSubmissionStore submissionStore;
    @Inject protected BundleSubmissionClient submissionClient;

    @Inject BundleProblemSubmissionResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listSubmissions(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Page<BundleSubmission> submissions = submissionStore.getSubmissions(problem.getJid(), pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(submissions.getPage(), BundleSubmission::getAuthorJid);
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        boolean canEdit = roleChecker.canEdit(actor, problem);
        boolean canSubmit = !roleChecker.canSubmit(actor, problem).isPresent();

        HtmlTemplate template = newProblemSubmissionTemplate(actor, problem);
        return new ListSubmissionsView(template, submissions, profilesMap, canEdit, canSubmit);
    }

    @POST
    @UnitOfWork
    public Response submit(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            MultivaluedMap<String, String> form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canSubmit(actor, problem));

        Set<String> enabledLanguages = statementStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);

        BundleAnswer answer = submissionClient.createBundleAnswerFromNewSubmission(form, language);
        submissionClient.submit(problem.getJid(), answer);

        return redirect("/problems/bundle/" + problemId + "/submissions");
    }

    @GET
    @Path("/regrade")
    @UnitOfWork
    public Response regradeSubmissions(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canSubmit(actor, problem));

        for (int pageNumber = 1; ; pageNumber++) {
            List<BundleSubmission> submissions = submissionStore.getSubmissions(problem.getJid(), pageNumber, PAGE_SIZE).getPage();
            if (submissions.isEmpty()) {
                break;
            }
            submissionClient.regradeSubmissions(submissions);
        }

        return redirect("/problems/bundle/" + problemId + "/submissions");
    }

    @GET
    @Path("/{submissionId}")
    @UnitOfWork(readOnly = true)
    public View viewSubmission(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("submissionId") int submissionId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        BundleSubmission submission = checkFound(submissionStore.getSubmissionById(submissionId));
        BundleAnswer answer = submissionClient.createBundleAnswerFromPastSubmission(submission.getJid());
        Profile profile = profileStore.getProfile(submission.getAuthorJid());

        HtmlTemplate template = newProblemSubmissionTemplate(actor, problem);
        return new ViewSubmissionView(template, submission, answer, profile);
    }

    @GET
    @Path("/{submissionId}/regrade")
    @UnitOfWork
    public Response regradeSubmission(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("submissionId") int submissionId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        BundleSubmission submission = checkFound(submissionStore.getSubmissionById(submissionId));
        submissionClient.regradeSubmission(submission);

        return redirect("/problems/bundle/" + problemId + "/submissions");
    }

    private HtmlTemplate newProblemSubmissionTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("submissions");
        return template;
    }
}
