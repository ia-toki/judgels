package judgels.michael.problem.bundle.submission;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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
    @Inject protected BundleSubmissionStore submissionStore;
    @Inject protected BundleSubmissionClient submissionClient;

    @Inject BundleProblemSubmissionResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listSubmissions(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @QueryParam("page") @DefaultValue("1") int pageIndex) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Page<BundleSubmission> submissions = submissionStore.getSubmissions(problem.getJid(), pageIndex);

        Set<String> userJids = submissions.getPage().stream().map(BundleSubmission::getAuthorJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        HtmlTemplate template = newProblemSubmissionTemplate(actor, problem);
        return new ListSubmissionsView(template, submissions, profilesMap);
    }

    @GET
    @Path("/{submissionId}")
    @UnitOfWork(readOnly = true)
    public View viewSubmission(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("submissionId") int submissionId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        BundleSubmission submission = checkFound(submissionStore.getSubmissionById(submissionId));
        BundleAnswer answer = submissionClient.createBundleAnswerFromPastSubmission(submission.getJid());
        Profile profile = profileStore.getProfile(Instant.now(), submission.getAuthorJid());

        HtmlTemplate template = newProblemSubmissionTemplate(actor, problem);
        return new ViewSubmissionView(template, submission, answer, profile);
    }

    private HtmlTemplate newProblemSubmissionTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("submissions");
        return template;
    }
}
