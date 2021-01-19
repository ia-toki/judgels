package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import static judgels.service.ServiceUtils.checkFound;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.submission.SubmissionFs;
import org.iatoki.judgels.sandalphon.problem.bundle.AbstractBundleProblemController;
import org.iatoki.judgels.sandalphon.problem.bundle.BundleProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.bundleSubmissionView;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.listSubmissionsView;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Result;

@Singleton
public final class BundleProblemSubmissionController extends AbstractBundleProblemController {

    private static final long PAGE_SIZE = 20;

    private final FileSystem bundleSubmissionFs;
    private final BundleSubmissionService bundleSubmissionService;
    private final ProblemService problemService;
    private final ProfileService profileService;

    @Inject
    public BundleProblemSubmissionController(@SubmissionFs FileSystem bundleSubmissionFs, BundleSubmissionService bundleSubmissionService, ProblemService problemService, ProfileService profileService) {
        this.bundleSubmissionFs = bundleSubmissionFs;
        this.bundleSubmissionService = bundleSubmissionService;
        this.problemService = problemService;
        this.profileService = profileService;
    }

    @Transactional
    public Result postSubmit(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem) && isClean) {
            return notFound();
        }

        DynamicForm dForm = formFactory.form().bindFromRequest();

        BundleAnswer bundleAnswer = bundleSubmissionService.createBundleAnswerFromNewSubmission(dForm, ProblemControllerUtils.getCurrentStatementLanguage());
        String submissionJid = bundleSubmissionService.submit(problem.getJid(), null, bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        bundleSubmissionService.storeSubmissionFiles(bundleSubmissionFs, null, submissionJid, bundleAnswer);

        return redirect(routes.BundleProblemSubmissionController.viewSubmissions(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result viewSubmissions(long problemId)  {
        return listSubmissions(problemId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, null, problem.getJid(), null);

        Set<String> userJids = pageOfBundleSubmissions.getPage().stream().map(BundleSubmission::getAuthorJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listSubmissionsView.render(pageOfBundleSubmissions, problemId, profilesMap, pageIndex, orderBy, orderDir));
        template.markBreadcrumbLocation("Submissions", org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.viewSubmissions(problemId));
        template.setPageTitle("Problem - Submissions");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    public Result viewSubmission(long problemId, long submissionId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        BundleSubmission bundleSubmission = checkFound(bundleSubmissionService.findBundleSubmissionById(submissionId));
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionFs, null, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Profile profile = profileService.getProfile(bundleSubmission.getAuthorJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(bundleSubmissionView.render(bundleSubmission, BundleSubmissionUtils.parseGradingResult(bundleSubmission), bundleAnswer, profile, null, problem.getSlug(), null));

        template.markBreadcrumbLocation("View submission", org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.viewSubmission(problemId, submissionId));
        template.setPageTitle("Problem - View submission");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional
    public Result regradeSubmission(long problemId, long submissionId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        BundleSubmission bundleSubmission = checkFound(bundleSubmissionService.findBundleSubmissionById(submissionId));
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionFs, null, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    @Transactional
    public Result regradeSubmissions(long problemId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            return notFound();
        }

        ListTableSelectionForm data = formFactory.form(ListTableSelectionForm.class).bindFromRequest().get();

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
                bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionFs, null, bundleSubmission.getJid());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.markBreadcrumbLocation("Submissions", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToSubmissions(problem.getId()));

        return super.renderTemplate(template, problemService, problem);
    }
}
