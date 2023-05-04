package judgels.michael.problem.programming.submission;

import static java.util.stream.Collectors.toSet;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.problem.programming.BaseProgrammingProblemResource;
import judgels.michael.template.HtmlTemplate;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("/problems/programming/{problemId}/submissions")
public class ProgrammingProblemSubmissionResource extends BaseProgrammingProblemResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected SubmissionStore submissionStore;
    @Inject protected SubmissionSourceBuilder submissionSourceBuilder;
    @Inject protected SubmissionClient submissionClient;
    @Inject protected SubmissionRegrader submissionRegrader;

    @Inject public ProgrammingProblemSubmissionResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listSubmissions(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Page<Submission> submissions = submissionStore.getSubmissions(Optional.empty(), Optional.empty(), Optional.of(problem.getJid()), pageNumber, PAGE_SIZE);
        Set<String> userJids = submissions.getPage().stream().map(Submission::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);
        Map<String, String> gradingLanguageNamesMap = GradingLanguageRegistry.getInstance().getLanguages();

        boolean canEdit = roleChecker.canEdit(actor, problem);
        boolean canSubmit = !roleChecker.canSubmit(actor, problem).isPresent();

        HtmlTemplate template = newProblemSubmissionTemplate(actor, problem);
        return new ListSubmissionsView(template, submissions, profilesMap, gradingLanguageNamesMap, canEdit, canSubmit);
    }

    @POST
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response submit(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            FormDataMultiPart parts) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canSubmit(actor, problem));

        String gradingEngine = parts.getField("gradingEngine").getValue();
        String gradingLanguage = parts.getField("gradingLanguage").getValue();

        GradingConfig gradingConfig = programmingProblemStore.getGradingConfig(null, problem.getJid());
        LanguageRestriction gradingLanguageRestriction = programmingProblemStore.getLanguageRestriction(null, problem.getJid());

        SubmissionSource source = submissionSourceBuilder.fromNewSubmission(parts);
        SubmissionData data = new SubmissionData.Builder()
                .problemJid(problem.getJid())
                .containerJid(problem.getJid())
                .gradingLanguage(gradingLanguage)
                .build();
        ProblemSubmissionConfig config = new ProblemSubmissionConfig.Builder()
                .sourceKeys(gradingConfig.getSourceFileFields())
                .gradingEngine(gradingEngine)
                .gradingLanguageRestriction(gradingLanguageRestriction)
                .build();
        Submission submission = submissionClient.submit(data, source, config);
        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);

        return redirect("/problems/programming/" + problemId + "/submissions");
    }

    @GET
    @Path("/regrade")
    @UnitOfWork
    public Response regradeSubmissions(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canSubmit(actor, problem));

        for (int pageNumber = 1; ; pageNumber++) {
            List<Submission> submissions = submissionStore.getSubmissions(Optional.empty(), Optional.empty(), Optional.of(problem.getJid()), pageNumber, PAGE_SIZE).getPage();
            if (submissions.isEmpty()) {
                break;
            }
            submissionRegrader.regradeSubmissions(submissions);
        }

        return redirect("/problems/programming/" + problemId + "/submissions");
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

        Submission submission = checkFound(submissionStore.getSubmissionById(submissionId));

        String gradingLanguageName = GradingLanguageRegistry.getInstance().get(submission.getGradingLanguage()).getName();
        SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());
        Profile profile = profileStore.getProfile(submission.getUserJid());

        GradingResultDetails details = null;
        if (submission.getLatestGrading().isPresent() && submission.getLatestGrading().get().getDetails().isPresent()) {
            details = submission.getLatestGrading().get().getDetails().get();
        }

        HtmlTemplate template = newProblemSubmissionTemplate(actor, problem);
        return new ViewSubmissionView(template, submission, Optional.ofNullable(details), source.getSubmissionFiles(), profile, gradingLanguageName);
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
        checkAllowed(roleChecker.canView(actor, problem));

        Submission submission = checkFound(submissionStore.getSubmissionById(submissionId));
        submissionRegrader.regradeSubmission(submission);

        return redirect("/problems/programming/" + problemId + "/submissions");
    }

    private HtmlTemplate newProblemSubmissionTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("submissions");
        return template;
    }
}
