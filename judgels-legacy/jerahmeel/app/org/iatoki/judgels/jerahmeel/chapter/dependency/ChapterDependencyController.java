package org.iatoki.judgels.jerahmeel.chapter.dependency;

import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.chapter.AbstractChapterController;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.chapter.dependency.html.listAddDependenciesView;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
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
public final class ChapterDependencyController extends AbstractChapterController {

    private static final long PAGE_SIZE = 20;
    private static final String DEPENDENCY = "dependency";
    private static final String CHAPTER = "chapter";

    private final ChapterDependencyService chapterDependencyService;
    private final ChapterService chapterService;

    @Inject
    public ChapterDependencyController(ChapterDependencyService chapterDependencyService, ChapterService chapterService) {
        this.chapterDependencyService = chapterDependencyService;
        this.chapterService = chapterService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewDependencies(long chapterId) throws ChapterNotFoundException {
        return listAddDependencies(chapterId, 0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listAddDependencies(long chapterId, long page, String orderBy, String orderDir, String filterString) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        Page<ChapterDependency> pageOfChapterDependencies = chapterDependencyService.getPageOfChapterDependencies(chapter.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
        Form<ChapterDependencyAddForm> chapterDependencyAddForm = Form.form(ChapterDependencyAddForm.class);

        return showListAddDependencies(chapter, chapterDependencyAddForm, pageOfChapterDependencies, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddDependency(long chapterId, long page, String orderBy, String orderDir, String filterString) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        Form<ChapterDependencyAddForm> chapterDependencyCreateForm = Form.form(ChapterDependencyAddForm.class).bindFromRequest();

        if (formHasErrors(chapterDependencyCreateForm)) {
            Page<ChapterDependency> pageOfChapterDependencies = chapterDependencyService.getPageOfChapterDependencies(chapter.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListAddDependencies(chapter, chapterDependencyCreateForm, pageOfChapterDependencies, orderBy, orderDir, filterString);
        }

        ChapterDependencyAddForm chapterDependencyAddData = chapterDependencyCreateForm.get();
        if (!chapterService.chapterExistsByJid(chapterDependencyAddData.chapterJid)) {
            chapterDependencyCreateForm.reject(Messages.get("error.chapter.invalidJid"));
            Page<ChapterDependency> pageOfChapterDependencies = chapterDependencyService.getPageOfChapterDependencies(chapter.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListAddDependencies(chapter, chapterDependencyCreateForm, pageOfChapterDependencies, orderBy, orderDir, filterString);
        }

        if (chapterDependencyService.existsByChapterJidAndDependencyJid(chapter.getJid(), chapterDependencyAddData.chapterJid)) {
            chapterDependencyCreateForm.reject(Messages.get("error.chapter.existChapter"));
            Page<ChapterDependency> pageOfChapterDependencies = chapterDependencyService.getPageOfChapterDependencies(chapter.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListAddDependencies(chapter, chapterDependencyCreateForm, pageOfChapterDependencies, orderBy, orderDir, filterString);
        }

        ChapterDependency chapterDependency = chapterDependencyService.addChapterDependency(chapter.getJid(), chapterDependencyAddData.chapterJid, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(CHAPTER, chapter.getJid(), chapter.getName(), DEPENDENCY, chapterDependency.getDependedChapterJid(), chapterDependency.getDependedChapterName()));

        return redirect(routes.ChapterDependencyController.viewDependencies(chapter.getId()));
    }

    @Transactional
    public Result removeDependency(long chapterId, long chapterDependencyId) throws ChapterNotFoundException, ChapterDependencyNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterDependency chapterDependency = chapterDependencyService.findChapterDependencyById(chapterDependencyId);

        if (!chapter.getJid().equals(chapterDependency.getChapterJid())) {
            return forbidden();
        }

        chapterDependencyService.removeChapterDependency(chapterDependencyId);

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.REMOVE_FROM.construct(CHAPTER, chapter.getJid(), chapter.getName(), DEPENDENCY, chapterDependency.getDependedChapterJid(), chapterDependency.getDependedChapterName()));

        return redirect(routes.ChapterDependencyController.viewDependencies(chapter.getId()));
    }

    private Result showListAddDependencies(Chapter chapter, Form<ChapterDependencyAddForm> chapterDependencyAddForm, Page<ChapterDependency> pageOfChapterDependencies, String orderBy, String orderDir, String filterString) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listAddDependenciesView.render(chapter.getId(), pageOfChapterDependencies, orderBy, orderDir, filterString, chapterDependencyAddForm));
        template.setPageTitle("Courses");

        return renderTemplate(template, chapter);
    }

    private Result renderTemplate(HtmlTemplate template, Chapter chapter) {
        appendTabs(template, chapter);
        
        template.markBreadcrumbLocation(Messages.get("chapter.dependencies"), routes.ChapterDependencyController.viewDependencies(chapter.getId()));

        return super.renderTemplate(template);
    }
}
