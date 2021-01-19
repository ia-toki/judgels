package org.iatoki.judgels.sandalphon.problem.programming.submission;

import static judgels.service.ServiceUtils.checkFound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.AbstractProgrammingProblemController;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.submission.html.listSubmissionsView;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemSubmissionController extends AbstractProgrammingProblemController {

    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;
    private final ProfileService profileService;
    private final SubmissionStore submissionStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final SubmissionClient submissionClient;
    private final SubmissionRegrader submissionRegrader;

    @Inject
    public ProgrammingProblemSubmissionController(
            ProblemService problemService,
            ProgrammingProblemService programmingProblemService,
            ProfileService profileService,
            SubmissionStore submissionStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            SubmissionClient submissionClient,
            SubmissionRegrader submissionRegrader) {
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
        this.profileService = profileService;
        this.submissionStore = submissionStore;
        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionClient = submissionClient;
        this.submissionRegrader = submissionRegrader;
    }

    @Transactional
    public Result postSubmit(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem) && isClean) {
            return notFound();
        }

        String gradingEngine;
        try {
            gradingEngine = programmingProblemService.getGradingEngine(null, problem.getJid());
        } catch (IOException e) {
            gradingEngine = GradingEngineRegistry.getInstance().getDefault();
        }

        GradingConfig gradingConfig;
        try {
            gradingConfig = programmingProblemService.getGradingConfig(null, problem.getJid());
        } catch (IOException e) {
            gradingConfig = GradingEngineRegistry.getInstance()
                    .get(gradingEngine)
                    .createDefaultConfig();
        }

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();

        String gradingLanguage = body.asFormUrlEncoded().get("language")[0];

        LanguageRestriction languageRestriction;
        try {
            languageRestriction = programmingProblemService.getLanguageRestriction(null, problem.getJid());
        } catch (IOException e) {
            languageRestriction = LanguageRestriction.noRestriction();
        }

        FormDataMultiPart parts = new FormDataMultiPart();
        try {
            for (Http.MultipartFormData.FilePart<File> part : body.getFiles()) {
                parts.getBodyParts().add(new FormDataBodyPart(
                        FormDataContentDisposition.name(part.getKey()).fileName(part.getFilename()).build(),
                        Files.readAllBytes(part.getRef().toPath()),
                        MediaType.MULTIPART_FORM_DATA_TYPE));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SubmissionSource source = submissionSourceBuilder.fromNewSubmission(parts);
        SubmissionData data = new SubmissionData.Builder()
                .problemJid(problem.getJid())
                .containerJid(problem.getJid())
                .gradingLanguage(gradingLanguage)
                .build();
        ProblemSubmissionConfig config = new ProblemSubmissionConfig.Builder()
                .sourceKeys(gradingConfig.getSourceFileFields())
                .gradingEngine(gradingEngine)
                .gradingLanguageRestriction(languageRestriction)
                .build();
        Submission submission = submissionClient.submit(data, source, config);
        submissionSourceBuilder.storeSubmissionSource(submission.getJid(), source);

        return redirect(routes.ProgrammingProblemSubmissionController.viewSubmissions(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result viewSubmissions(long problemId)  {
        return listSubmissions(problemId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Page<Submission> pageOfProgrammingSubmissions = submissionStore.getSubmissions(Optional.empty(), Optional.empty(), Optional.of(problem.getJid()), Optional.of((int) pageIndex + 1));
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getNamesMap();

        Set<String> userJids = pageOfProgrammingSubmissions.getPage().stream().map(Submission::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listSubmissionsView.render(pageOfProgrammingSubmissions, gradingLanguageToNameMap, problemId, profilesMap, pageIndex, orderBy, orderDir));
        template.markBreadcrumbLocation("Submissions", routes.ProgrammingProblemSubmissionController.viewSubmissions(problemId));
        template.setPageTitle("Problem - Submissions");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    public Result viewSubmission(long problemId, long submissionId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Submission programmingSubmission = submissionStore.getSubmissionById(submissionId).get();

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        SubmissionSource submissionSource = submissionSourceBuilder.fromPastSubmission(programmingSubmission.getJid());

        Profile profile = profileService.getProfile(programmingSubmission.getUserJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).renderViewSubmission(programmingSubmission, submissionSource, profile, null, problem.getSlug(), GradingLanguageRegistry.getInstance().get(programmingSubmission.getGradingLanguage()).getName(), null));

        template.markBreadcrumbLocation("View submission", routes.ProgrammingProblemSubmissionController.viewSubmission(problemId, submissionId));
        template.setPageTitle("Problem - View submission");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional
    public Result regradeSubmission(long problemId, long submissionId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Submission programmingSubmission = submissionStore.getSubmissionById(submissionId).get();
        submissionRegrader.regradeSubmission(programmingSubmission);

        return redirect(routes.ProgrammingProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    @Transactional
    public Result regradeSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        ListTableSelectionForm data = formFactory.form(ListTableSelectionForm.class).bindFromRequest().get();

        List<Submission> programmingSubmissions;

        if (data.selectAll) {
            programmingSubmissions = submissionStore.getSubmissions(Optional.empty(), Optional.empty(), Optional.of(problem.getJid()), Optional.empty()).getPage();
        } else if (data.selectJids != null) {
            programmingSubmissions = submissionStore.getSubmissionByJids(data.selectJids);
        } else {
            return redirect(routes.ProgrammingProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
        }
        submissionRegrader.regradeSubmissions(programmingSubmissions);

        return redirect(routes.ProgrammingProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.markBreadcrumbLocation("Submissions", org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToSubmissions(problem.getId()));

        return super.renderTemplate(template, problemService, problem);
    }
}
