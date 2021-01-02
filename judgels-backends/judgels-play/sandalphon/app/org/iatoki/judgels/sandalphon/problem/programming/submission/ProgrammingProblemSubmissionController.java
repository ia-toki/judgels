package org.iatoki.judgels.sandalphon.problem.programming.submission;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.submission.programming.Submission;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.submission.SubmissionFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.programming.AbstractProgrammingProblemController;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestrictionAdapter;
import org.iatoki.judgels.sandalphon.problem.programming.submission.html.listSubmissionsView;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemSubmissionController extends AbstractProgrammingProblemController {

    private static final long PAGE_SIZE = 20;

    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;
    private final FileSystemProvider programmingSubmissionFileSystemProvider;
    private final ProgrammingSubmissionService programmingSubmissionService;

    @Inject
    public ProgrammingProblemSubmissionController(ProblemService problemService, ProgrammingProblemService programmingProblemService, @SubmissionFileSystemProvider FileSystemProvider programmingSubmissionFileSystemProvider, ProgrammingSubmissionService programmingSubmissionService) {
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
        this.programmingSubmissionFileSystemProvider = programmingSubmissionFileSystemProvider;
        this.programmingSubmissionService = programmingSubmissionService;
    }

    @Transactional
    public Result postSubmit(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem) && isClean) {
            return notFound();
        }

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(null, problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();

        String gradingLanguage = body.asFormUrlEncoded().get("language")[0];

        LanguageRestriction languageRestriction;
        try {
            languageRestriction = programmingProblemService.getLanguageRestriction(null, problem.getJid());
        } catch (IOException e) {
            languageRestriction = LanguageRestriction.noRestriction();
        }
        Set<String> allowedLanguageNames = LanguageRestrictionAdapter.getFinalAllowedLanguageNames(ImmutableList.of(languageRestriction));

        try {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromNewSubmission(body);
            String submissionJid = programmingSubmissionService.submit(problem.getJid(), null, engine, gradingLanguage, allowedLanguageNames, submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            ProgrammingSubmissionUtils.storeSubmissionFiles(programmingSubmissionFileSystemProvider, null, submissionJid, submissionSource);
        } catch (ProgrammingSubmissionException e) {
            flash("submissionError", e.getMessage());
            return redirect(org.iatoki.judgels.sandalphon.problem.programming.statement.routes.ProgrammingProblemStatementController.viewStatement(problem.getId()));
        }

        return redirect(routes.ProgrammingProblemSubmissionController.viewSubmissions(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result viewSubmissions(long problemId) throws ProblemNotFoundException  {
        return listSubmissions(problemId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Page<Submission> pageOfProgrammingSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, null, problem.getJid(), null);
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getNamesMap();

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listSubmissionsView.render(pageOfProgrammingSubmissions, gradingLanguageToNameMap, problemId, pageIndex, orderBy, orderDir));
        template.markBreadcrumbLocation("Submissions", routes.ProgrammingProblemSubmissionController.viewSubmissions(problemId));
        template.setPageTitle("Problem - Submissions");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    public Result viewSubmission(long problemId, long submissionId) throws ProblemNotFoundException, ProgrammingSubmissionNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Submission programmingSubmission = programmingSubmissionService.findProgrammingSubmissionById(submissionId);

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionFileSystemProvider, null, programmingSubmission.getJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).renderViewSubmission(programmingSubmission, submissionSource, JidCacheServiceImpl.getInstance().getDisplayName(programmingSubmission.getUserJid()), null, problem.getSlug(), GradingLanguageRegistry.getInstance().get(programmingSubmission.getGradingLanguage()).getName(), null));

        template.markBreadcrumbLocation("View submission", routes.ProgrammingProblemSubmissionController.viewSubmission(problemId, submissionId));
        template.setPageTitle("Problem - View submission");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional
    public Result regradeSubmission(long problemId, long submissionId, long pageIndex, String orderBy, String orderDir) throws ProblemNotFoundException, ProgrammingSubmissionNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Submission programmingSubmission = programmingSubmissionService.findProgrammingSubmissionById(submissionId);
        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionFileSystemProvider, null, programmingSubmission.getJid());
        programmingSubmissionService.regrade(programmingSubmission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.ProgrammingProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    @Transactional
    public Result regradeSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        ListTableSelectionForm data = formFactory.form(ListTableSelectionForm.class).bindFromRequest().get();

        List<Submission> programmingSubmissions;

        if (data.selectAll) {
            programmingSubmissions = programmingSubmissionService.getProgrammingSubmissionsByFilters(orderBy, orderDir, null, problem.getJid(), null);
        } else if (data.selectJids != null) {
            programmingSubmissions = programmingSubmissionService.getProgrammingSubmissionsByJids(data.selectJids);
        } else {
            return redirect(routes.ProgrammingProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
        }

        for (Submission programmingSubmission : programmingSubmissions) {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionFileSystemProvider, null, programmingSubmission.getJid());
            programmingSubmissionService.regrade(programmingSubmission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return redirect(routes.ProgrammingProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.markBreadcrumbLocation("Submissions", org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToSubmissions(problem.getId()));

        return super.renderTemplate(template, problemService, problem);
    }
}
