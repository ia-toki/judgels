package org.iatoki.judgels.jerahmeel.chapter.submission.programming;

import com.google.common.collect.Lists;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.languages.GradingLanguageRegistry;
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
import org.iatoki.judgels.jerahmeel.chapter.submission.programming.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.jerahmeel.user.item.UserItem;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionException;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionNotFoundException;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class ChapterProgrammingSubmissionController extends AbstractChapterSubmissionController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String PROGRAMMING_FILES = "programming_files";
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";

    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;
    private final FileSystemProvider submissionLocalFileSystemProvider;
    private final FileSystemProvider submissionRemoteFileSystemProvider;
    private final ProgrammingSubmissionService submissionService;
    private final UserItemService userItemService;

    @Inject
    public ChapterProgrammingSubmissionController(ChapterProblemService chapterProblemService, ChapterService chapterService, @ProgrammingSubmissionLocalFileSystemProvider FileSystemProvider submissionLocalFileSystemProvider, @ProgrammingSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider submissionRemoteFileSystemProvider, ProgrammingSubmissionService submissionService, UserItemService userItemService) {
        this.chapterProblemService = chapterProblemService;
        this.chapterService = chapterService;
        this.submissionLocalFileSystemProvider = submissionLocalFileSystemProvider;
        this.submissionRemoteFileSystemProvider = submissionRemoteFileSystemProvider;
        this.submissionService = submissionService;
        this.userItemService = userItemService;
    }

    @Transactional
    public Result postSubmitProblem(long chapterId, String problemJid) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(chapter.getJid(), problemJid);

        Http.MultipartFormData body = request().body().asMultipartFormData();

        String gradingLanguage = body.asFormUrlEncoded().get("language")[0];
        String gradingEngine = body.asFormUrlEncoded().get("engine")[0];

        String submissionJid;
        try {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromNewSubmission(body);
            submissionJid = submissionService.submit(problemJid, chapter.getJid(), gradingEngine, gradingLanguage, null, submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            ProgrammingSubmissionUtils.storeSubmissionFiles(submissionLocalFileSystemProvider, submissionRemoteFileSystemProvider, submissionJid, submissionSource);

        } catch (ProgrammingSubmissionException e) {
            flash("submissionError", e.getMessage());

            return redirect(org.iatoki.judgels.jerahmeel.chapter.problem.routes.ChapterProblemController.viewChapterProblem(chapterId, chapterProblem.getId()));
        }

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.SUBMIT.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, chapterProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid())), SUBMISSION, submissionJid, PROGRAMMING_FILES));

        return redirect(routes.ChapterProgrammingSubmissionController.viewSubmissions(chapterId));
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

        Page<ProgrammingSubmission> pageOfSubmissions = submissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, actualUserJid, actualProblemJid, chapter.getJid());
        Map<String, String> problemJidToAliasMap = chapterProblemService.getProgrammingProblemJidToAliasMapByChapterJid(chapter.getJid());
        List<UserItem> userItems = userItemService.getUserItemsByItemJid(chapter.getJid());
        List<String> userJids = Lists.transform(userItems, u -> u.getUserJid());
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getNamesMap();

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listSubmissionsView.render(chapter.getId(), pageOfSubmissions, userJids, problemJidToAliasMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        template.setSecondaryTitle(Messages.get("submission.submissions"));
        template.setPageTitle("Chapters - Programming Submissions");

        return renderTemplate(template, chapter);
    }

    @Transactional(readOnly = true)
    public Result viewSubmission(long chapterId, long submissionId) throws ChapterNotFoundException, ProgrammingSubmissionNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ProgrammingSubmission submission = submissionService.findProgrammingSubmissionById(submissionId);

        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(submissionLocalFileSystemProvider, submissionRemoteFileSystemProvider, submission.getJid());
        String authorName = JidCacheServiceImpl.getInstance().getDisplayName(submission.getAuthorJid());
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(chapter.getJid(), submission.getProblemJid());
        String chapterProblemAlias = chapterProblem.getAlias();
        String chapterProblemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid()), StatementControllerUtils.getCurrentStatementLanguage());
        String gradingLanguageName = GradingLanguageRegistry.getInstance().get(submission.getGradingLanguage()).getName();

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(submission.getGradingEngine()).renderViewSubmission(submission, submissionSource, authorName, chapterProblemAlias, chapterProblemName, gradingLanguageName, chapter.getName()));
        template.markBreadcrumbLocation(chapterProblemAlias, routes.ChapterProgrammingSubmissionController.viewSubmission(chapter.getId(), submission.getId()));
        template.setPageTitle("Chapters - Programming Submissions - View");

        return renderTemplate(template, chapter);
    }

    @Transactional
    public Result regradeSubmission(long chapterId, long submissionId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ChapterNotFoundException, ProgrammingSubmissionNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        ProgrammingSubmission submission = submissionService.findProgrammingSubmissionById(submissionId);
        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(submissionLocalFileSystemProvider, submissionRemoteFileSystemProvider, submission.getJid());
        submissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, submission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(submission.getProblemJid()), SUBMISSION, submission.getJid(), submission.getId() + ""));

        return redirect(routes.ChapterProgrammingSubmissionController.listSubmissions(chapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    @Transactional
    public Result regradeSubmissions(long chapterId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ChapterNotFoundException, ProgrammingSubmissionNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        ListTableSelectionForm data = Form.form(ListTableSelectionForm.class).bindFromRequest().get();

        List<ProgrammingSubmission> submissions;

        if (data.selectAll) {
            submissions = submissionService.getProgrammingSubmissionsByFilters(orderBy, orderDir, userJid, problemJid, chapter.getJid());
        } else if (data.selectJids != null) {
            submissions = submissionService.getProgrammingSubmissionsByJids(data.selectJids);
        } else {
            return redirect(routes.ChapterProgrammingSubmissionController.listSubmissions(chapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
        }

        for (ProgrammingSubmission submission : submissions) {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(submissionLocalFileSystemProvider, submissionRemoteFileSystemProvider, submission.getJid());
            submissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

            JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, submission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(submission.getProblemJid()), SUBMISSION, submission.getJid(), submission.getId() + ""));
        }

        return redirect(routes.ChapterProgrammingSubmissionController.listSubmissions(chapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    protected Result renderTemplate(HtmlTemplate template, Chapter chapter) {
        template.markBreadcrumbLocation(Messages.get("chapter.submissions.programming"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToProgrammingSubmissions(chapter.getId()));

        return super.renderTemplate(template, chapter);
    }
}
