package org.iatoki.judgels.jerahmeel.problemset.submission.bundle;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.DeprecatedControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.activity.JerahmeelActivityKeys;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSet;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetControllerUtils;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetNotFoundException;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblem;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemService;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemStatus;
import org.iatoki.judgels.jerahmeel.problemset.submission.AbstractProblemSetSubmissionController;
import org.iatoki.judgels.jerahmeel.problemset.submission.bundle.html.listOwnSubmissionsView;
import org.iatoki.judgels.jerahmeel.problemset.submission.bundle.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.problemset.submission.bundle.html.listSubmissionsWithActionsView;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionNotFoundException;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.bundleSubmissionView;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public final class ProblemSetBundleSubmissionController extends AbstractProblemSetSubmissionController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String BUNDLE_ANSWER = "bundle answer";
    private static final String PROBLEM = "problem";
    private static final String PROBLEM_SET = "problem set";

    private final FileSystemProvider bundleSubmissionLocalFileSystemProvider;
    private final FileSystemProvider bundleSubmissionRemoteFileSystemProvider;
    private final BundleSubmissionService bundleSubmissionService;
    private final ProblemSetProblemService problemSetProblemService;
    private final ProblemSetService problemSetService;

    @Inject
    public ProblemSetBundleSubmissionController(@BundleSubmissionLocalFileSystemProvider FileSystemProvider bundleSubmissionLocalFileSystemProvider, @BundleSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider bundleSubmissionRemoteFileSystemProvider, BundleSubmissionService bundleSubmissionService, ProblemSetProblemService problemSetProblemService, ProblemSetService problemSetService) {
        this.bundleSubmissionLocalFileSystemProvider = bundleSubmissionLocalFileSystemProvider;
        this.bundleSubmissionRemoteFileSystemProvider = bundleSubmissionRemoteFileSystemProvider;
        this.bundleSubmissionService = bundleSubmissionService;
        this.problemSetProblemService = problemSetProblemService;
        this.problemSetService = problemSetService;
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional
    public Result postSubmitProblem(long problemSetId, String problemJid) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemByProblemSetJidAndProblemJid(problemSet.getJid(), problemJid);

        if (problemSetProblem.getStatus() != ProblemSetProblemStatus.VISIBLE) {
            return notFound();
        }

        DynamicForm dForm = DynamicForm.form().bindFromRequest();

        BundleAnswer bundleAnswer = bundleSubmissionService.createBundleAnswerFromNewSubmission(dForm, ProblemSetControllerUtils.getCurrentStatementLanguage());
        String submissionJid = bundleSubmissionService.submit(problemSetProblem.getProblemJid(), problemSet.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        bundleSubmissionService.storeSubmissionFiles(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, submissionJid, bundleAnswer);

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.SUBMIT.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, problemSetProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid())), SUBMISSION, submissionJid, BUNDLE_ANSWER));

        return redirect(routes.ProblemSetBundleSubmissionController.viewOwnSubmissions(problemSet.getId()));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewOwnSubmissions(long problemSetId) throws ProblemSetNotFoundException {
        return listOwnSubmissions(problemSetId, 0, "id", "desc", null);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result listOwnSubmissions(long problemSetId, long pageIndex, String orderBy, String orderDir, String problemJid) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        String actualProblemJid = "(none)".equals(problemJid) ? null : problemJid;

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, IdentityUtils.getUserJid(), actualProblemJid, problemSet.getJid());
        Map<String, String> problemJidToAliasMap = problemSetProblemService.getBundleProblemJidToAliasMapByProblemSetJid(problemSet.getJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listOwnSubmissionsView.render(problemSet.getId(), pageOfBundleSubmissions, problemJidToAliasMap, pageIndex, orderBy, orderDir, actualProblemJid));
        template.setSecondaryTitle(Messages.get("submission.submissions"));
        template.setPageTitle("Problem Sets - Bundle Submissions");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewSubmissions(long problemSetId) throws ProblemSetNotFoundException {
        return listSubmissions(problemSetId, 0, "id", "desc", null, null);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listSubmissions(long problemSetId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        String actualUserJid = "(none)".equals(userJid) ? null : userJid;
        String actualProblemJid = "(none)".equals(problemJid) ? null : problemJid;

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, actualUserJid, actualProblemJid, problemSet.getJid());
        Map<String, String> problemJidToAliasMap = problemSetProblemService.getBundleProblemJidToAliasMapByProblemSetJid(problemSet.getJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        if (JerahmeelControllerUtils.getInstance().isAdmin()) {
            template.setContent(listSubmissionsWithActionsView.render(problemSet.getId(), pageOfBundleSubmissions, ImmutableList.of(), problemJidToAliasMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        } else {
            template.setContent(listSubmissionsView.render(problemSet.getId(), pageOfBundleSubmissions, ImmutableList.of(), problemJidToAliasMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        }
        template.setSecondaryTitle(Messages.get("submission.submissions"));
        template.markBreadcrumbLocation(Messages.get("archive.problemSet.submissions.bundle.all"), routes.ProblemSetBundleSubmissionController.viewSubmissions(problemSet.getId()));
        template.setPageTitle("Problem Sets - Bundle Submissions");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewSubmission(long problemSetId, long bundleSubmissionId) throws ProblemSetNotFoundException, BundleSubmissionNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);

        if (!JerahmeelControllerUtils.getInstance().isAdmin() && !bundleSubmission.getAuthorJid().equals(IdentityUtils.getUserJid())) {
            return redirect(routes.ProblemSetBundleSubmissionController.viewOwnSubmissions(problemSetId));
        }

        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemByProblemSetJidAndProblemJid(problemSet.getJid(), bundleSubmission.getProblemJid());
        String problemSetProblemAlias = problemSetProblem.getAlias();
        String problemSetProblemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid()), DeprecatedControllerUtils.getHardcodedDefaultLanguage());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(bundleSubmissionView.render(bundleSubmission, BundleSubmissionUtils.parseGradingResult(bundleSubmission), bundleAnswer, JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getAuthorJid()), problemSetProblemAlias, problemSetProblemName, problemSet.getName()));
        template.markBreadcrumbLocation(bundleSubmission.getId() + "", routes.ProblemSetBundleSubmissionController.viewSubmission(problemSet.getId(), bundleSubmission.getId()));
        template.setPageTitle("Problem Sets - Bundle Submissions - View");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmission(long problemSetId, long bundleSubmissionId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ProblemSetNotFoundException, BundleSubmissionNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, bundleSubmission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getProblemJid()), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));

        return redirect(routes.ProblemSetBundleSubmissionController.listSubmissions(problemSetId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmissions(long problemSetId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ProblemSetNotFoundException, BundleSubmissionNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        ListTableSelectionForm listTableSelectionData = Form.form(ListTableSelectionForm.class).bindFromRequest().get();
        List<BundleSubmission> bundleSubmissions;

        if (listTableSelectionData.selectAll) {
            bundleSubmissions = bundleSubmissionService.getBundleSubmissionsByFilters(orderBy, orderDir, userJid, problemJid, problemSet.getJid());
        } else if (listTableSelectionData.selectJids != null) {
            bundleSubmissions = bundleSubmissionService.getBundleSubmissionsByJids(listTableSelectionData.selectJids);
        } else {
            return redirect(routes.ProblemSetBundleSubmissionController.listSubmissions(problemSetId, pageIndex, orderBy, orderDir, userJid, problemJid));
        }

        for (BundleSubmission bundleSubmission : bundleSubmissions) {
            BundleAnswer bundleAnswer;
            try {
                bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

            JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, bundleSubmission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getProblemJid()), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));
        }

        return redirect(routes.ProblemSetBundleSubmissionController.listSubmissions(problemSetId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemSet problemSet) {
        appendTabs(template, problemSet);

        if (!JerahmeelUtils.isGuest()) {
            template.addTertiaryTab(Messages.get("archive.problemSet.submissions.bundle.own"), routes.ProblemSetBundleSubmissionController.viewOwnSubmissions(problemSet.getId()));
            template.addTertiaryTab(Messages.get("archive.problemSet.submissions.bundle.all"), routes.ProblemSetBundleSubmissionController.viewSubmissions(problemSet.getId()));
        }
        template.markBreadcrumbLocation(Messages.get("archive.problemSet.submissions.bundle"), routes.ProblemSetBundleSubmissionController.viewOwnSubmissions(problemSet.getId()));

        return super.renderTemplate(template, problemSet);
    }
}
