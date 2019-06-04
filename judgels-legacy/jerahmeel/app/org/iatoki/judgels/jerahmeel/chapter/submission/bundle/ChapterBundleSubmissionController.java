package org.iatoki.judgels.jerahmeel.chapter.submission.bundle;

import com.google.common.collect.Lists;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.StatementControllerUtils;
import org.iatoki.judgels.jerahmeel.activity.JerahmeelActivityKeys;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblem;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemService;
import org.iatoki.judgels.jerahmeel.chapter.submission.AbstractChapterSubmissionController;
import org.iatoki.judgels.jerahmeel.chapter.submission.bundle.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.jerahmeel.user.item.UserItem;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
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

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class ChapterBundleSubmissionController extends AbstractChapterSubmissionController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String BUNDLE_ANSWER = "bundle answer";
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";

    private final FileSystemProvider bundleSubmissionLocalFileSystemProvider;
    private final FileSystemProvider bundleSubmissionRemoteFileSystemProvider;
    private final BundleSubmissionService bundleSubmissionService;
    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;
    private final UserItemService userItemService;

    @Inject
    public ChapterBundleSubmissionController(@BundleSubmissionLocalFileSystemProvider FileSystemProvider bundleSubmissionLocalFileSystemProvider, @BundleSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider bundleSubmissionRemoteFileSystemProvider, BundleSubmissionService bundleSubmissionService, ChapterProblemService chapterProblemService, ChapterService chapterService, UserItemService userItemService) {
        this.bundleSubmissionLocalFileSystemProvider = bundleSubmissionLocalFileSystemProvider;
        this.bundleSubmissionRemoteFileSystemProvider = bundleSubmissionRemoteFileSystemProvider;
        this.bundleSubmissionService = bundleSubmissionService;
        this.chapterService = chapterService;
        this.chapterProblemService = chapterProblemService;
        this.userItemService = userItemService;
    }

    @Transactional
    public Result postSubmitProblem(long chapterId, String problemJid) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(chapter.getJid(), problemJid);

        DynamicForm dForm = DynamicForm.form().bindFromRequest();

        BundleAnswer answer = bundleSubmissionService.createBundleAnswerFromNewSubmission(dForm, StatementControllerUtils.getCurrentStatementLanguage());
        String submissionJid = bundleSubmissionService.submit(chapterProblem.getProblemJid(), chapter.getJid(), answer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        bundleSubmissionService.storeSubmissionFiles(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, submissionJid, answer);

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.SUBMIT.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, chapterProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid())), SUBMISSION, submissionJid, BUNDLE_ANSWER));

        return redirect(routes.ChapterBundleSubmissionController.viewSubmissions(chapterId));
    }

    @Transactional(readOnly = true)
    public Result viewSubmissions(long chapterId) throws ChapterNotFoundException {
        return listSubmissions(chapterId, 0, "id", "desc", null, null);
    }

    @Transactional(readOnly = true)
    public Result listSubmissions(long chapterId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        String actualUserJid = "(none)".equals(userJid) ? null : userJid;
        String actualProblemJid = "(none)".equals(problemJid) ? null : problemJid;

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, actualUserJid, actualProblemJid, chapter.getJid());
        Map<String, String> problemJidToAliasMap = chapterProblemService.getBundleProblemJidToAliasMapByChapterJid(chapter.getJid());
        List<UserItem> userItems = userItemService.getUserItemsByItemJid(chapter.getJid());
        List<String> userJids = Lists.transform(userItems, u -> u.getUserJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listSubmissionsView.render(chapter.getId(), pageOfBundleSubmissions, userJids, problemJidToAliasMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        template.setSecondaryTitle(Messages.get("submission.submissions"));
        template.setPageTitle("Chapters - Programming BundleSubmissions");

        return renderTemplate(template, chapter);
    }

    @Transactional(readOnly = true)
    public Result viewSubmission(long chapterId, long bundleSubmissionId) throws ChapterNotFoundException, BundleSubmissionNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(chapter.getJid(), bundleSubmission.getProblemJid());
        String chapterProblemAlias = chapterProblem.getAlias();
        String chapterProblemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid()), StatementControllerUtils.getCurrentStatementLanguage());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(bundleSubmissionView.render(bundleSubmission, BundleSubmissionUtils.parseGradingResult(bundleSubmission), bundleAnswer, JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getAuthorJid()), chapterProblemAlias, chapterProblemName, chapter.getName()));
        template.markBreadcrumbLocation(chapterProblemAlias, routes.ChapterBundleSubmissionController.viewSubmission(chapter.getId(), bundleSubmission.getId()));
        template.setPageTitle("Chapters - Programming BundleSubmissions - View");

        return renderTemplate(template, chapter);
    }

    @Transactional
    public Result regradeSubmission(long chapterId, long bundleSubmissionId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ChapterNotFoundException, BundleSubmissionNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, bundleSubmission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getProblemJid()), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));

        return redirect(routes.ChapterBundleSubmissionController.listSubmissions(chapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    @Transactional
    public Result regradeSubmissions(long chapterId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ChapterNotFoundException, BundleSubmissionNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        ListTableSelectionForm listTableSelectionData = Form.form(ListTableSelectionForm.class).bindFromRequest().get();
        List<BundleSubmission> bundleSubmissions;

        if (listTableSelectionData.selectAll) {
            bundleSubmissions = bundleSubmissionService.getBundleSubmissionsByFilters(orderBy, orderDir, userJid, problemJid, chapter.getJid());
        } else if (listTableSelectionData.selectJids != null) {
            bundleSubmissions = bundleSubmissionService.getBundleSubmissionsByJids(listTableSelectionData.selectJids);
        } else {
            return redirect(routes.ChapterBundleSubmissionController.listSubmissions(chapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
        }

        for (BundleSubmission bundleSubmission : bundleSubmissions) {
            BundleAnswer bundleAnswer;
            try {
                bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

            JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, bundleSubmission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getProblemJid()), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));
        }

        return redirect(routes.ChapterBundleSubmissionController.listSubmissions(chapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    protected Result renderTemplate(HtmlTemplate template, Chapter chapter) {
        template.markBreadcrumbLocation(Messages.get("chapter.submissions.bundle"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToBundleSubmissions(chapter.getId()));

        return super.renderTemplate(template, chapter);
    }
}
