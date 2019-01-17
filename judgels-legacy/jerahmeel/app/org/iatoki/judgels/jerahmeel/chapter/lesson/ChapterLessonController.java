package org.iatoki.judgels.jerahmeel.chapter.lesson;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.RemoteException;
import judgels.sandalphon.api.client.lesson.ClientLessonService;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonLessonStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.StatementControllerUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterControllerUtils;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.chapter.lesson.html.addChapterLessonView;
import org.iatoki.judgels.jerahmeel.chapter.lesson.html.editChapterLessonView;
import org.iatoki.judgels.jerahmeel.chapter.lesson.html.listChapterLessonsView;
import org.iatoki.judgels.jerahmeel.chapter.lesson.html.viewLessonView;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.sandalphon.SandalphonResourceDisplayNames;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class ChapterLessonController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String LESSON = "lesson";
    private static final String CHAPTER = "chapter";

    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientLessonService clientLessonService;
    private final SandalphonClientAPI sandalphonClientAPI;
    private final ChapterLessonService chapterLessonService;
    private final ChapterService chapterService;

    @Inject
    public ChapterLessonController(@Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader, ClientLessonService clientLessonService, SandalphonClientAPI sandalphonClientAPI, ChapterLessonService chapterLessonService, ChapterService chapterService) {
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientLessonService = clientLessonService;
        this.sandalphonClientAPI = sandalphonClientAPI;
        this.chapterLessonService = chapterLessonService;
        this.chapterService = chapterService;
    }

    @Transactional(readOnly = true)
    public Result viewChapterLessons(long chapterId) throws ChapterNotFoundException {
        return listChapterLessons(chapterId, 0, "alias", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listChapterLessons(long chapterId, long page, String orderBy, String orderDir, String filterString) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        Page<ChapterLesson> pageOfChapterLessons = chapterLessonService.getPageOfChapterLessons(chapter.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
        List<String> lessonJids = pageOfChapterLessons.getData().stream().map(cp -> cp.getLessonJid()).collect(Collectors.toList());
        Map<String, String> lessonSlugsMap = SandalphonResourceDisplayNameUtils.buildSlugsMap(JidCacheServiceImpl.getInstance().getDisplayNames(lessonJids));

        return showListChapterLessons(chapter, pageOfChapterLessons, orderBy, orderDir, filterString, lessonSlugsMap);
    }

    @Transactional(readOnly = true)
    public Result viewChapterLesson(long chapterId, long chapterLessonId) throws ChapterNotFoundException, ChapterLessonNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterLesson chapterLesson = chapterLessonService.findChapterLessonById(chapterLessonId);

        if (!chapter.getJid().equals(chapterLesson.getChapterJid())) {
            return forbidden();
        }

        SandalphonLessonStatementRenderRequestParam param = new SandalphonLessonStatementRenderRequestParam();

        param.setCurrentMillis(System.currentTimeMillis());
        param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
        param.setSwitchStatementLanguageUrl(routes.ChapterLessonController.switchLanguage().absoluteURL(request(), request().secure()));

        String requestUrl = sandalphonClientAPI.getLessonStatementRenderAPIEndpoint(chapterLesson.getLessonJid());
        String requestBody = sandalphonClientAPI.constructLessonStatementRenderAPIRequestBody(chapterLesson.getLessonJid(), param);

        LazyHtml content = new LazyHtml(viewLessonView.render(requestUrl, requestBody));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter,
                new InternalLink(chapterLesson.getAlias(), routes.ChapterLessonController.viewChapterLesson(chapter.getId(), chapterLesson.getId()))
        );

        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Lesson - View");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    public Result renderImage(long chapterId, long chapterLessonId, String imageFilename) throws ChapterNotFoundException, ChapterLessonNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterLesson chapterLesson = chapterLessonService.findChapterLessonById(chapterLessonId);

        if (!chapter.getJid().equals(chapterLesson.getChapterJid())) {
            return notFound();
        }

        String imageUrl = sandalphonClientAPI.getLessonStatementMediaRenderAPIEndpoint(chapterLesson.getLessonJid(), imageFilename);

        return redirect(imageUrl);
    }

    public Result switchLanguage() {
        String languageCode = DynamicForm.form().bindFromRequest().get("langCode");
        StatementControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeader("Referer"));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addChapterLesson(long chapterId) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        Form<ChapterLessonAddForm> chapterLessonAddForm = Form.form(ChapterLessonAddForm.class);

        return showAddChapterLesson(chapter, chapterLessonAddForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddChapterLesson(long chapterId) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        Form<ChapterLessonAddForm> chapterLessonCreateForm = Form.form(ChapterLessonAddForm.class).bindFromRequest();

        if (formHasErrors(chapterLessonCreateForm)) {
            return showAddChapterLesson(chapter, chapterLessonCreateForm);
        }

        ChapterLessonAddForm chapterLessonCreateData = chapterLessonCreateForm.get();

        if (chapterLessonService.aliasExistsInChapter(chapter.getJid(), chapterLessonCreateData.alias)) {
            chapterLessonCreateForm.reject(Messages.get("error.chapter.lesson.aliasExist"));

            return showAddChapterLesson(chapter, chapterLessonCreateForm);
        }

        Map<String, String> lessonSlugToJidMap = clientLessonService.translateAllowedSlugsToJids(sandalphonClientAuthHeader, IdentityUtils.getUserJid(), ImmutableSet.of(chapterLessonCreateData.lessonSlug));
        if (!lessonSlugToJidMap.containsKey(chapterLessonCreateData.lessonSlug)) {
            chapterLessonCreateForm.reject(Messages.get("error.lesson.invalid"));
            return showAddChapterLesson(chapter, chapterLessonCreateForm);
        }

        String lessonJid = lessonSlugToJidMap.get(chapterLessonCreateData.lessonSlug);

        LessonInfo lesson;
        try {
            lesson = clientLessonService.getLesson(sandalphonClientAuthHeader, lessonJid);
        } catch (RemoteException e) {
            if (e.getStatus() >= Http.Status.INTERNAL_SERVER_ERROR) {
                chapterLessonCreateForm.reject(Messages.get("error.system.sandalphon.connection"));
            } else {
                chapterLessonCreateForm.reject(Messages.get("error.lesson.invalid"));
            }
            return showAddChapterLesson(chapter, chapterLessonCreateForm);
        }

        chapterLessonService.addChapterLesson(chapter.getJid(), lessonJid, chapterLessonCreateData.alias, ChapterLessonStatus.valueOf(chapterLessonCreateData.status), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        JidCacheServiceImpl.getInstance().putDisplayName(lessonJid, SandalphonResourceDisplayNames.getLessonDisplayName(lesson), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(CHAPTER, chapter.getJid(), chapter.getName(), LESSON, lessonJid, lesson.getSlug()));

        return redirect(routes.ChapterLessonController.viewChapterLessons(chapter.getId()));
    }

    @Transactional
    public Result removeChapterLesson(long chapterId, long chapterLessonId) throws ChapterNotFoundException, ChapterLessonNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterLesson chapterLesson = chapterLessonService.findChapterLessonById(chapterLessonId);

        if (!chapter.getJid().equals(chapterLesson.getChapterJid())) {
            return forbidden();
        }

        chapterLessonService.removeChapterLesson(chapterLessonId);

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.REMOVE_FROM.construct(CHAPTER, chapter.getJid(), chapter.getName(), LESSON, chapterLesson.getLessonJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterLesson.getLessonJid()))));

        return redirect(routes.ChapterLessonController.viewChapterLessons(chapter.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editChapterLesson(long chapterId, long chapterLessonId) throws ChapterNotFoundException, ChapterLessonNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterLesson chapterLesson = chapterLessonService.findChapterLessonById(chapterLessonId);

        if (!chapter.getJid().equals(chapterLesson.getChapterJid())) {
            return notFound();
        }

        ChapterLessonEditForm chapterLessonEditData = new ChapterLessonEditForm();
        chapterLessonEditData.alias = chapterLesson.getAlias();
        chapterLessonEditData.status = chapterLesson.getStatus().name();

        Form<ChapterLessonEditForm> chapterLessonEditForm = Form.form(ChapterLessonEditForm.class).fill(chapterLessonEditData);

        return showEditChapterLesson(chapter, chapterLesson, chapterLessonEditForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditChapterLesson(long chapterId, long chapterLessonId) throws ChapterNotFoundException, ChapterLessonNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterLesson chapterLesson = chapterLessonService.findChapterLessonById(chapterLessonId);

        if (!chapter.getJid().equals(chapterLesson.getChapterJid())) {
            return notFound();
        }

        Form<ChapterLessonEditForm> chapterLessonEditForm = Form.form(ChapterLessonEditForm.class).bindFromRequest();
        if (formHasErrors(chapterLessonEditForm)) {
            return showEditChapterLesson(chapter, chapterLesson, chapterLessonEditForm);
        }

        ChapterLessonEditForm chapterLessonEditData = chapterLessonEditForm.get();
        if (!chapterLessonEditData.alias.equals(chapterLesson.getAlias()) && chapterLessonService.aliasExistsInChapter(chapter.getJid(), chapterLessonEditData.alias)) {
            chapterLessonEditForm.reject(Messages.get("error.chapter.lesson.duplicateAlias"));

            return showEditChapterLesson(chapter, chapterLesson, chapterLessonEditForm);
        }

        chapterLessonService.updateChapterLesson(chapterLesson.getId(), chapterLessonEditData.alias, ChapterLessonStatus.valueOf(chapterLessonEditData.status), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT_IN.construct(CHAPTER, chapter.getJid(), chapter.getName(), LESSON, chapterLesson.getLessonJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterLesson.getLessonJid()))));

        return redirect(routes.ChapterLessonController.viewChapterLessons(chapter.getId()));
    }


    private Result showListChapterLessons(Chapter chapter, Page<ChapterLesson> currentPage, String orderBy, String orderDir, String filterString, Map<String, String> lessonSlugsMap) {
        LazyHtml content = new LazyHtml(listChapterLessonsView.render(chapter.getId(), currentPage, orderBy, orderDir, filterString, lessonSlugsMap));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("chapter.lessons"), new InternalLink(Messages.get("commons.add"), routes.ChapterLessonController.addChapterLesson(chapter.getId())), c));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Lessons");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showAddChapterLesson(Chapter chapter, Form<ChapterLessonAddForm> form) {
        LazyHtml content = new LazyHtml(addChapterLessonView.render(chapter.getId(), form));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter,
              new InternalLink(Messages.get("commons.add"), routes.ChapterLessonController.addChapterLesson(chapter.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Lessons - Create");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditChapterLesson(Chapter chapter, ChapterLesson chapterLesson, Form<ChapterLessonEditForm> form) {
        LazyHtml content = new LazyHtml(editChapterLessonView.render(form, chapter.getId(), chapterLesson));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter,
                new InternalLink(Messages.get("commons.update"), routes.ChapterLessonController.editChapterLesson(chapter.getId(), chapterLesson.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Lessons - Edit");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Chapter chapter, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ChapterControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("chapter.lessons"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToLessons(chapter.getId())));
        breadcrumbsBuilder.add(new InternalLink(Messages.get("commons.view"), routes.ChapterLessonController.viewChapterLessons(chapter.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
