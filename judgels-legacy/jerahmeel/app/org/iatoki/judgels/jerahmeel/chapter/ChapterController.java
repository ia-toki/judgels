package org.iatoki.judgels.jerahmeel.chapter;

import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.chapter.html.createChapterView;
import org.iatoki.judgels.jerahmeel.chapter.html.editChapterGeneralView;
import org.iatoki.judgels.jerahmeel.chapter.html.listChaptersView;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class ChapterController extends AbstractChapterController {

    private static final long PAGE_SIZE = 20;
    private static final String CHAPTER = "chapter";

    private final ChapterService chapterService;
    private final UserItemService userItemService;

    @Inject
    public ChapterController(ChapterService chapterService, UserItemService userItemService) {
        this.chapterService = chapterService;
        this.userItemService = userItemService;
    }

    @Transactional(readOnly = true)
    public Result viewChapters() {
        return listChapters(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listChapters(long page, String orderBy, String orderDir, String filterString) {
        Page<Chapter> pageOfChapters = chapterService.getPageOfChapters(page, PAGE_SIZE, orderBy, orderDir, filterString);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listChaptersView.render(pageOfChapters, orderBy, orderDir, filterString));
        template.setMainTitle(Messages.get("chapter.list"));
        template.addMainButton(Messages.get("commons.create"), routes.ChapterController.createChapter());
        template.setPageTitle("Chapters");

        return renderTemplate(template);
    }

    public Result jumpToLessons(long chapterId) {
        return redirect(org.iatoki.judgels.jerahmeel.chapter.lesson.routes.ChapterLessonController.viewChapterLessons(chapterId));
    }

    public Result jumpToProblems(long chapterId) {
        return redirect(org.iatoki.judgels.jerahmeel.chapter.problem.routes.ChapterProblemController.viewChapterProblems(chapterId));
    }

    public Result jumpToSubmissions(long chapterId) {
        return redirect(org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToProgrammingSubmissions(chapterId));
    }

    public Result jumpToBundleSubmissions(long chapterId) {
        return redirect(org.iatoki.judgels.jerahmeel.chapter.submission.bundle.routes.ChapterBundleSubmissionController.viewSubmissions(chapterId));
    }

    public Result jumpToProgrammingSubmissions(long chapterId) {
        return redirect(org.iatoki.judgels.jerahmeel.chapter.submission.programming.routes.ChapterProgrammingSubmissionController.viewSubmissions(chapterId));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createChapter() {
        Form<ChapterUpsertForm> chapterUpsertForm = Form.form(ChapterUpsertForm.class);

        return showCreateChapter(chapterUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateChapter() {
        Form<ChapterUpsertForm> chapterUpsertForm = Form.form(ChapterUpsertForm.class).bindFromRequest();

        if (formHasErrors(chapterUpsertForm)) {
            return showCreateChapter(chapterUpsertForm);
        }

        ChapterUpsertForm chapterUpsertData = chapterUpsertForm.get();
        Chapter chapter = chapterService.createChapter(chapterUpsertData.name, chapterUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(CHAPTER, chapter.getJid(), chapter.getName()));

        return redirect(routes.ChapterController.viewChapters());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editChapterGeneral(long chapterId) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterUpsertForm chapterUpsertData = new ChapterUpsertForm();
        chapterUpsertData.name = chapter.getName();
        chapterUpsertData.description = chapter.getDescription();

        Form<ChapterUpsertForm> chapterUpsertForm = Form.form(ChapterUpsertForm.class).fill(chapterUpsertData);

        if (!userItemService.userItemExistsByUserJidAndItemJidAndStatus(IdentityUtils.getUserJid(), chapter.getJid(), UserItemStatus.VIEWED)) {
            userItemService.upsertUserItem(IdentityUtils.getUserJid(), chapter.getJid(), UserItemStatus.VIEWED, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return showEditChapterGeneral(chapterUpsertForm, chapter);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditChapterGeneral(long chapterId) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        Form<ChapterUpsertForm> chapterUpsertForm = Form.form(ChapterUpsertForm.class).bindFromRequest();

        if (formHasErrors(chapterUpsertForm)) {
            return showEditChapterGeneral(chapterUpsertForm, chapter);
        }

        ChapterUpsertForm chapterUpsertData = chapterUpsertForm.get();
        chapterService.updateChapter(chapter.getJid(), chapterUpsertData.name, chapterUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (!chapter.getName().equals(chapterUpsertData.name)) {
            JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.RENAME.construct(CHAPTER, chapter.getJid(), chapter.getName(), chapterUpsertData.name));
        }
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(CHAPTER, chapter.getJid(), chapterUpsertData.name));

        return redirect(routes.ChapterController.viewChapters());
    }

    private Result showCreateChapter(Form<ChapterUpsertForm> chapterUpsertForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createChapterView.render(chapterUpsertForm));
        template.setMainTitle(Messages.get("chapter.create"));
        template.markBreadcrumbLocation(Messages.get("chapter.create"), routes.ChapterController.createChapter());
        template.setPageTitle("Chapter - Create");
        return renderTemplate(template);
    }

    private Result showEditChapterGeneral(Form<ChapterUpsertForm> chapterUpsertForm, Chapter chapter) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editChapterGeneralView.render(chapterUpsertForm, chapter.getId()));
        appendTabs(template, chapter);
        template.markBreadcrumbLocation(Messages.get("chapter.update"), routes.ChapterController.editChapterGeneral(chapter.getId()));
        template.setPageTitle("Chapter - Update");
        return renderTemplate(template);
    }
}
