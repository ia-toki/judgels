package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.activity.SandalphonActivityKeys;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.submission.SubmissionFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.bundle.AbstractBundleProblemController;
import org.iatoki.judgels.sandalphon.problem.bundle.BundleProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.bundleSubmissionView;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.listSubmissionsView;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class BundleProblemSubmissionController extends AbstractBundleProblemController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String PROBLEM = "problem";
    private static final String BUNDLE_ANSWER = "bundle answer";

    private final FileSystemProvider bundleSubmissionFileSystemProvider;
    private final BundleSubmissionService bundleSubmissionService;
    private final ProblemService problemService;

    @Inject
    public BundleProblemSubmissionController(@SubmissionFileSystemProvider FileSystemProvider bundleSubmissionFileSystemProvider, BundleSubmissionService bundleSubmissionService, ProblemService problemService) {
        this.bundleSubmissionFileSystemProvider = bundleSubmissionFileSystemProvider;
        this.bundleSubmissionService = bundleSubmissionService;
        this.problemService = problemService;
    }

    @Transactional
    public Result postSubmit(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem) && isClean) {
            return notFound();
        }

        DynamicForm dForm = Form.form().bindFromRequest();

        BundleAnswer bundleAnswer = bundleSubmissionService.createBundleAnswerFromNewSubmission(dForm, ProblemControllerUtils.getCurrentStatementLanguage());
        String submissionJid = bundleSubmissionService.submit(problem.getJid(), null, bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        bundleSubmissionService.storeSubmissionFiles(bundleSubmissionFileSystemProvider, null, submissionJid, bundleAnswer);

        SandalphonControllerUtils.getInstance().addActivityLog(SandalphonActivityKeys.SUBMIT.construct(PROBLEM, problem.getJid(), problem.getSlug(), SUBMISSION, submissionJid, BUNDLE_ANSWER));

        return redirect(routes.BundleProblemSubmissionController.viewSubmissions(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result viewSubmissions(long problemId) throws ProblemNotFoundException  {
        return listSubmissions(problemId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, null, problem.getJid(), null);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listSubmissionsView.render(pageOfBundleSubmissions, problemId, pageIndex, orderBy, orderDir));
        template.markBreadcrumbLocation(Messages.get("problem.bundle.submission.list"), org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.viewSubmissions(problemId));
        template.setPageTitle("Problem - Submissions");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    public Result viewSubmission(long problemId, long submissionId) throws ProblemNotFoundException, BundleSubmissionNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(submissionId);
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionFileSystemProvider, null, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(bundleSubmissionView.render(bundleSubmission, BundleSubmissionUtils.parseGradingResult(bundleSubmission), bundleAnswer, JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getAuthorJid()), null, problem.getSlug(), null));

        template.markBreadcrumbLocation(Messages.get("problem.programming.submission.view"), org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.viewSubmission(problemId, submissionId));
        template.setPageTitle("Problem - View Submission");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional
    public Result regradeSubmission(long problemId, long submissionId, long pageIndex, String orderBy, String orderDir) throws ProblemNotFoundException, BundleSubmissionNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(submissionId);
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionFileSystemProvider, null, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(SandalphonActivityKeys.REGRADE.construct(PROBLEM, problem.getJid(), problem.getSlug(), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));

        return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    @Transactional
    public Result regradeSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        ListTableSelectionForm data = Form.form(ListTableSelectionForm.class).bindFromRequest().get();

        List<BundleSubmission> submissions;

        if (data.selectAll) {
            submissions = bundleSubmissionService.getBundleSubmissionsByFilters(orderBy, orderDir, null, problem.getJid(), null);
        } else if (data.selectJids != null) {
            submissions = bundleSubmissionService.getBundleSubmissionsByJids(data.selectJids);
        } else {
            return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
        }

        for (BundleSubmission bundleSubmission : submissions) {
            BundleAnswer bundleAnswer;
            try {
                bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionFileSystemProvider, null, bundleSubmission.getJid());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

            SandalphonControllerUtils.getInstance().addActivityLog(SandalphonActivityKeys.REGRADE.construct(PROBLEM, problem.getJid(), problem.getSlug(), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));
        }

        return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.markBreadcrumbLocation(Messages.get("problem.bundle.submission"), org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToSubmissions(problem.getId()));
    
        return super.renderTemplate(template, problemService, problem);
    }
}
