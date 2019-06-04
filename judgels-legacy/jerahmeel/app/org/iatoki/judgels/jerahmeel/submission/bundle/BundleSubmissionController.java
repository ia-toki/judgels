package org.iatoki.judgels.jerahmeel.submission.bundle;

import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.DeprecatedControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.activity.JerahmeelActivityKeys;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblem;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemService;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JerahmeelJidUtils;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSet;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblem;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemService;
import org.iatoki.judgels.jerahmeel.submission.AbstractSubmissionController;
import org.iatoki.judgels.jerahmeel.submission.bundle.html.listOwnSubmissionsView;
import org.iatoki.judgels.jerahmeel.submission.bundle.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.submission.bundle.html.listSubmissionsWithActionsView;
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
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class BundleSubmissionController extends AbstractSubmissionController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";
    private static final String PROBLEM_SET = "problem set";

    private final FileSystemProvider bundleSubmissionLocalFileSystemProvider;
    private final FileSystemProvider bundleSubmissionRemoteFileSystemProvider;
    private final BundleSubmissionService bundleSubmissionService;
    private final ProblemSetProblemService problemSetProblemService;
    private final ProblemSetService problemSetService;
    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;

    @Inject
    public BundleSubmissionController(@BundleSubmissionLocalFileSystemProvider FileSystemProvider bundleSubmissionLocalFileSystemProvider, @BundleSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider bundleSubmissionRemoteFileSystemProvider, BundleSubmissionService bundleSubmissionService, ProblemSetProblemService problemSetProblemService, ProblemSetService problemSetService, ChapterProblemService chapterProblemService, ChapterService chapterService) {
        this.bundleSubmissionLocalFileSystemProvider = bundleSubmissionLocalFileSystemProvider;
        this.bundleSubmissionRemoteFileSystemProvider = bundleSubmissionRemoteFileSystemProvider;
        this.bundleSubmissionService = bundleSubmissionService;
        this.problemSetProblemService = problemSetProblemService;
        this.problemSetService = problemSetService;
        this.chapterProblemService = chapterProblemService;
        this.chapterService = chapterService;
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewOwnSubmissions() {
        return listOwnSubmissions(0, "id", "desc");
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result listOwnSubmissions(long pageIndex, String orderBy, String orderDir) {
        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, IdentityUtils.getUserJid(), null, null);
        List<String> problemJids = pageOfBundleSubmissions.getData().stream().map(s -> s.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), "en-US");
        Map<String, String> jidToNameMap = getJidToNameMap(chapterService, problemSetService, pageOfBundleSubmissions.getData().stream().map(s -> s.getContainerJid()).collect(Collectors.toList()));

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listOwnSubmissionsView.render(pageOfBundleSubmissions, jidToNameMap, problemTitlesMap, pageIndex, orderBy, orderDir));
        appendOwnSubtabs(template);
        appendTabs(template);
        template.setMainTitle(Messages.get("submission.submissions"));
        template.markBreadcrumbLocation(Messages.get("submission.own"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToOwnSubmissions());
        template.markBreadcrumbLocation(Messages.get("submission.bundle"), routes.BundleSubmissionController.viewOwnSubmissions());
        template.setPageTitle("Submissions - Bundle");

        return renderTemplate(template);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewOwnSubmission(long bundleSubmissionId) throws BundleSubmissionNotFoundException {
        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);

        if (!(JerahmeelControllerUtils.getInstance().isAdmin() || bundleSubmission.getAuthorJid().equals(IdentityUtils.getUserJid()))) {
            return notFound();
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(getViewSubmissionContent(bundleSubmission));

        appendOwnSubtabs(template);
        appendTabs(template);
        template.markBreadcrumbLocation(Messages.get("submission.own"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToOwnSubmissions());
        template.markBreadcrumbLocation(Messages.get("submission.bundle"), routes.BundleSubmissionController.viewOwnSubmissions());
        template.markBreadcrumbLocation(bundleSubmission.getId() + "", routes.BundleSubmissionController.viewSubmission(bundleSubmission.getId()));
        template.setPageTitle("Submissions - Bundle - View");

        return renderTemplate(template);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewSubmissions() {
        return listSubmissions(0, "id", "desc");
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listSubmissions(long pageIndex, String orderBy, String orderDir) {
        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, null, null, null);
        List<String> problemJids = pageOfBundleSubmissions.getData().stream().map(s -> s.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), "en-US");
        Map<String, String> jidToNameMap = getJidToNameMap(chapterService, problemSetService, pageOfBundleSubmissions.getData().stream().map(s -> s.getContainerJid()).collect(Collectors.toList()));

        HtmlTemplate template = getBaseHtmlTemplate();
        if (JerahmeelControllerUtils.getInstance().isAdmin()) {
            template.setContent(listSubmissionsWithActionsView.render(pageOfBundleSubmissions, jidToNameMap, problemTitlesMap, pageIndex, orderBy, orderDir));
        } else {
            template.setContent(listSubmissionsView.render(pageOfBundleSubmissions, jidToNameMap, problemTitlesMap, pageIndex, orderBy, orderDir));
        }
        appendAllSubtabs(template);
        if (!JerahmeelUtils.isGuest()) {
            appendTabs(template);
        }
        template.setMainTitle(Messages.get("submission.submissions"));
        template.markBreadcrumbLocation(Messages.get("submission.all"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToAllSubmissions());
        template.markBreadcrumbLocation(Messages.get("submission.bundle"), routes.BundleSubmissionController.viewSubmissions());
        template.setPageTitle("Submissions - Bundle");

        return renderTemplate(template);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewSubmission(long bundleSubmissionId) throws BundleSubmissionNotFoundException {
        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);

        if (!(JerahmeelControllerUtils.getInstance().isAdmin() || bundleSubmission.getAuthorJid().equals(IdentityUtils.getUserJid()))) {
            return notFound();
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(getViewSubmissionContent(bundleSubmission));

        appendOwnSubtabs(template);
        appendTabs(template);
        template.markBreadcrumbLocation(Messages.get("submission.all"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToAllSubmissions());
        template.markBreadcrumbLocation(Messages.get("submission.bundle"), routes.BundleSubmissionController.viewSubmissions());
        template.markBreadcrumbLocation(bundleSubmission.getId() + "", routes.BundleSubmissionController.viewSubmission(bundleSubmission.getId()));
        template.setPageTitle("Submissions - Bundle - View");

        return renderTemplate(template);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmission(String containerJid, long bundleSubmissionId, long pageIndex, String orderBy, String orderDir) throws BundleSubmissionNotFoundException {
        String containerName = "";
        String logKey = "";
        if (containerJid.startsWith(JerahmeelJidUtils.PROBLEM_SET_JID_PREFIX)) {
            if (!problemSetService.problemSetExistsByJid(containerJid)) {
                return notFound();
            }

            ProblemSet problemSet = problemSetService.findProblemSetByJid(containerJid);
            containerName = problemSet.getName();
            logKey = PROBLEM_SET;
        } else {
            if (!chapterService.chapterExistsByJid(containerJid)) {
                return notFound();
            }

            Chapter chapter = chapterService.findChapterByJid(containerJid);
            containerName = chapter.getName();
            logKey = CHAPTER;
        }

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(logKey, containerJid, containerName, PROBLEM, bundleSubmission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getProblemJid()), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));

        return redirect(routes.BundleSubmissionController.listSubmissions(pageIndex, orderBy, orderDir));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmissions(long pageIndex, String orderBy, String orderDir) throws BundleSubmissionNotFoundException {
        ListTableSelectionForm listTableSelectionData = Form.form(ListTableSelectionForm.class).bindFromRequest().get();
        List<BundleSubmission> bundleSubmissions;

        if (listTableSelectionData.selectJids != null) {
            bundleSubmissions = bundleSubmissionService.getBundleSubmissionsByJids(listTableSelectionData.selectJids);
        } else {
            return redirect(routes.BundleSubmissionController.listSubmissions(pageIndex, orderBy, orderDir));
        }

        for (BundleSubmission bundleSubmission : bundleSubmissions) {
            BundleAnswer bundleAnswer;
            try {
                bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return redirect(routes.BundleSubmissionController.listSubmissions(pageIndex, orderBy, orderDir));
    }

    private Html getViewSubmissionContent(BundleSubmission bundleSubmission) {
        String containerJid;
        String containerName;
        String problemAlias;
        String problemName;
        if (bundleSubmission.getContainerJid().startsWith(JerahmeelJidUtils.PROBLEM_SET_JID_PREFIX)) {
            ProblemSet problemSet = problemSetService.findProblemSetByJid(bundleSubmission.getContainerJid());
            containerJid = problemSet.getJid();
            containerName = problemSet.getName();

            ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemByProblemSetJidAndProblemJid(containerJid, bundleSubmission.getProblemJid());
            problemAlias = problemSetProblem.getAlias();
            problemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid()), DeprecatedControllerUtils.getHardcodedDefaultLanguage());
        } else {
            Chapter chapter = chapterService.findChapterByJid(bundleSubmission.getContainerJid());
            containerJid = chapter.getJid();
            containerName = chapter.getName();

            ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(containerJid, bundleSubmission.getProblemJid());
            problemAlias = chapterProblem.getAlias();
            problemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid()), DeprecatedControllerUtils.getHardcodedDefaultLanguage());
        }

        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bundleSubmissionView.render(bundleSubmission, BundleSubmissionUtils.parseGradingResult(bundleSubmission), bundleAnswer, JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getAuthorJid()), problemAlias, problemName, containerName);
    }
}
