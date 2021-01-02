package org.iatoki.judgels.sandalphon.lesson;

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.html.createLessonView;
import org.iatoki.judgels.sandalphon.lesson.html.editLessonView;
import org.iatoki.judgels.sandalphon.lesson.html.listLessonsView;
import org.iatoki.judgels.sandalphon.lesson.html.viewLessonView;
import org.iatoki.judgels.sandalphon.lesson.statement.LessonStatementUtils;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

@Singleton
public final class LessonController extends AbstractLessonController {

    private static final long PAGE_SIZE = 20;

    private final LessonService lessonService;

    @Inject
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listLessons(0, "updatedAt", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listLessons(long pageIndex, String sortBy, String orderBy, String filterString) {
        boolean isAdmin = SandalphonControllerUtils.getInstance().isAdmin();
        boolean isWriter = SandalphonControllerUtils.getInstance().isWriter();
        Page<Lesson> pageOfLessons = lessonService.getPageOfLessons(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString, IdentityUtils.getUserJid(), isAdmin);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listLessonsView.render(pageOfLessons, sortBy, orderBy, filterString, isWriter));
        template.setMainTitle("Lessons");
        if (isWriter) {
            template.addMainButton("Create", routes.LessonController.createLesson());
        }
        template.markBreadcrumbLocation("Lessons", routes.LessonController.index());
        template.setPageTitle("Lessons");

        return renderTemplate(template);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createLesson() {
        Form<LessonCreateForm> lessonCreateForm = formFactory.form(LessonCreateForm.class);

        return showCreateLesson(lessonCreateForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateLesson() {
        Form<LessonCreateForm> lessonCreateForm = formFactory.form(LessonCreateForm.class).bindFromRequest();

        if (formHasErrors(lessonCreateForm)) {
            return showCreateLesson(lessonCreateForm);
        }

        if (lessonService.lessonExistsBySlug(lessonCreateForm.get().slug)) {
            return showCreateLesson(lessonCreateForm.withError("slug", "Slug already exists"));
        }

        LessonCreateForm lessonCreateData = lessonCreateForm.get();

        Lesson lesson;
        try {
            lesson = lessonService.createLesson(lessonCreateData.slug, lessonCreateData.additionalNote, lessonCreateData.initLanguageCode, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            lessonService.updateStatement(null, lesson.getJid(), lessonCreateData.initLanguageCode, new LessonStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(lessonCreateData.initLanguageCode))
                    .text(LessonStatementUtils.getDefaultText(lessonCreateData.initLanguageCode))
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lessonService.initRepository(IdentityUtils.getUserJid(), lesson.getJid());

        LessonControllerUtils.setCurrentStatementLanguage(lessonCreateData.initLanguageCode);

        return redirect(routes.LessonController.index());
    }

    public Result enterLesson(long lessonId) {
        return redirect(routes.LessonController.jumpToStatement(lessonId));
    }

    public Result jumpToStatement(long lessonId) {
        return redirect(org.iatoki.judgels.sandalphon.lesson.statement.routes.LessonStatementController.viewStatement(lessonId));
    }

    public Result jumpToVersions(long lessonId) {
        return redirect(org.iatoki.judgels.sandalphon.lesson.version.routes.LessonVersionController.viewVersionLocalChanges(lessonId));
    }

    public Result jumpToPartners(long lessonId) {
        return redirect(org.iatoki.judgels.sandalphon.lesson.partner.routes.LessonPartnerController.viewPartners(lessonId));
    }

    @Transactional(readOnly = true)
    public Result viewLesson(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(viewLessonView.render(lesson));
        template.setMainTitle("#" + lesson.getId() + ": " + lesson.getSlug());
        template.addMainButton("Enter lesson", routes.LessonController.enterLesson(lesson.getId()));
        template.markBreadcrumbLocation("View lesson", routes.LessonController.viewLesson(lesson.getId()));
        template.setPageTitle("Lesson - View");

        return renderLessonTemplate(template, lessonService, lesson);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editLesson(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            return redirect(routes.LessonController.viewLesson(lesson.getId()));
        }

        LessonEditForm lessonEditData = new LessonEditForm();
        lessonEditData.slug = lesson.getSlug();
        lessonEditData.additionalNote = lesson.getAdditionalNote();

        Form<LessonEditForm> lessonEditForm = formFactory.form(LessonEditForm.class).fill(lessonEditData);

        return showEditLesson(lessonEditForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditLesson(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            return notFound();
        }

        Form<LessonEditForm> lessonEditForm = formFactory.form(LessonEditForm.class).bindFromRequest();

        if (formHasErrors(lessonEditForm)) {
            return showEditLesson(lessonEditForm, lesson);
        }

        if (!lesson.getSlug().equals(lessonEditForm.get().slug) && lessonService.lessonExistsBySlug(lessonEditForm.get().slug)) {
            return showEditLesson(lessonEditForm.withError("slug", "Slug already exists"), lesson);
        }

        LessonEditForm lessonEditData = lessonEditForm.get();
        lessonService.updateLesson(lesson.getJid(), lessonEditData.slug, lessonEditData.additionalNote, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.LessonController.viewLesson(lesson.getId()));
    }

    @RequireCSRFCheck
    public Result switchLanguage(long lessonId) {
        String languageCode = formFactory.form().bindFromRequest().get("langCode");
        LessonControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeaders().get("Referer").orElse(""));
    }

    private Result showCreateLesson(Form<LessonCreateForm> lessonCreateForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createLessonView.render(lessonCreateForm));
        template.setMainTitle("Create lesson");
        template.markBreadcrumbLocation("Create lesson", routes.LessonController.createLesson());
        template.setPageTitle("Lesson - Create");

        return renderTemplate(template);
    }

    private Result showEditLesson(Form<LessonEditForm> lessonEditForm, Lesson lesson) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editLessonView.render(lessonEditForm, lesson));
        template.setMainTitle("#" + lesson.getId() + ": " + lesson.getSlug());
        template.addMainButton("Enter lesson", routes.LessonController.enterLesson(lesson.getId()));
        template.markBreadcrumbLocation("Update lesson", routes.LessonController.editLesson(lesson.getId()));
        template.setPageTitle("Lesson - Update");

        return renderLessonTemplate(template, lessonService, lesson);
    }

    protected Result renderLessonTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        appendVersionLocalChangesWarning(template, lessonService, lesson);
        template.addSecondaryTab("View", routes.LessonController.viewLesson(lesson.getId()));

        if (LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            template.addSecondaryTab("Update", routes.LessonController.editLesson(lesson.getId()));
        }

        template.markBreadcrumbLocation("Lessons", routes.LessonController.index());

        return super.renderTemplate(template);
    }
}
