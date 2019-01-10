package org.iatoki.judgels.sandalphon.lesson;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.sandalphon.lesson.statement.LessonStatement;
import org.iatoki.judgels.sandalphon.lesson.statement.LessonStatementUtils;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.lesson.html.createLessonView;
import org.iatoki.judgels.sandalphon.lesson.html.editLessonView;
import org.iatoki.judgels.sandalphon.lesson.html.listLessonsView;
import org.iatoki.judgels.sandalphon.lesson.html.viewLessonView;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
@Named
public final class LessonController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String LESSON = "lesson";

    private final LessonService lessonService;

    @Inject
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listLessons(0, "timeUpdate", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listLessons(long pageIndex, String sortBy, String orderBy, String filterString) {
        Page<Lesson> pageOfLessons = lessonService.getPageOfLessons(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString, IdentityUtils.getUserJid(), SandalphonControllerUtils.getInstance().isAdmin());

        LazyHtml content = new LazyHtml(listLessonsView.render(pageOfLessons, sortBy, orderBy, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("lesson.list"), new InternalLink(Messages.get("commons.create"), routes.LessonController.createLesson()), c));

        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("lesson.lessons"), routes.LessonController.index())
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lessons");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createLesson() {
        Form<LessonCreateForm> lessonCreateForm = Form.form(LessonCreateForm.class);

        return showCreateLesson(lessonCreateForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateLesson() {
        Form<LessonCreateForm> lessonCreateForm = Form.form(LessonCreateForm.class).bindFromRequest();

        if (formHasErrors(lessonCreateForm)) {
            return showCreateLesson(lessonCreateForm);
        }

        if (lessonService.lessonExistsBySlug(lessonCreateForm.get().slug)) {
            lessonCreateForm.reject("slug", Messages.get("error.lesson.slugExists"));
        }

        LessonCreateForm lessonCreateData = lessonCreateForm.get();

        Lesson lesson;
        try {
            lesson = lessonService.createLesson(lessonCreateData.slug, lessonCreateData.additionalNote, lessonCreateData.initLanguageCode, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            lessonService.updateStatement(null, lesson.getJid(), lessonCreateData.initLanguageCode, new LessonStatement(ProblemStatementUtils.getDefaultTitle(lessonCreateData.initLanguageCode), LessonStatementUtils.getDefaultText(lessonCreateData.initLanguageCode)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lessonService.initRepository(IdentityUtils.getUserJid(), lesson.getJid());

        LessonControllerUtils.setCurrentStatementLanguage(lessonCreateData.initLanguageCode);

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(LESSON, lesson.getJid(), lessonCreateData.slug));

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

    public Result jumpToClients(long lessonId) {
        return redirect(org.iatoki.judgels.sandalphon.lesson.client.routes.LessonClientController.editClientLessons(lessonId));
    }

    @Transactional(readOnly = true)
    public Result viewLesson(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        LazyHtml content = new LazyHtml(viewLessonView.render(lesson));
        appendSubtabs(content, lesson);
        LessonControllerUtils.appendVersionLocalChangesWarningLayout(content, lessonService, lesson);
        content.appendLayout(c -> headingWithActionLayout.render("#" + lesson.getId() + ": " + lesson.getSlug(), new InternalLink(Messages.get("lesson.enter"), routes.LessonController.enterLesson(lesson.getId())), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                LessonControllerUtils.getLessonBreadcrumbsBuilder(lesson)
                        .add(new InternalLink(Messages.get("lesson.view"), routes.LessonController.viewLesson(lesson.getId())))
                        .build()
        );
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - View");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
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

        Form<LessonEditForm> lessonEditForm = Form.form(LessonEditForm.class).fill(lessonEditData);

        return showEditLesson(lessonEditForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditLesson(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            return notFound();
        }

        Form<LessonEditForm> lessonEditForm = Form.form(LessonEditForm.class).bindFromRequest();

        if (formHasErrors(lessonEditForm)) {
            return showEditLesson(lessonEditForm, lesson);
        }

        if (!lesson.getSlug().equals(lessonEditForm.get().slug) && lessonService.lessonExistsBySlug(lessonEditForm.get().slug)) {
            lessonEditForm.reject("slug", Messages.get("error.lesson.slugExists"));
        }

        LessonEditForm lessonEditData = lessonEditForm.get();
        lessonService.updateLesson(lesson.getJid(), lessonEditData.slug, lessonEditData.additionalNote, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (!lesson.getSlug().equals(lessonEditData.slug)) {
            SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.RENAME.construct(LESSON, lesson.getJid(), lesson.getSlug(), lessonEditData.slug));
        }
        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT.construct(LESSON, lesson.getJid(), lessonEditData.slug));

        return redirect(routes.LessonController.viewLesson(lesson.getId()));
    }

    public Result switchLanguage(long lessonId) {
        String languageCode = DynamicForm.form().bindFromRequest().get("langCode");
        LessonControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeader("Referer"));
    }

    private Result showCreateLesson(Form<LessonCreateForm> lessonCreateForm) {
        LazyHtml content = new LazyHtml(createLessonView.render(lessonCreateForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("lesson.create"), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("lesson.lessons"), routes.LessonController.index()),
                new InternalLink(Messages.get("lesson.create"), routes.LessonController.createLesson())
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - Create");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditLesson(Form<LessonEditForm> lessonEditForm, Lesson lesson) {
        LazyHtml content = new LazyHtml(editLessonView.render(lessonEditForm, lesson));
        appendSubtabs(content, lesson);
        LessonControllerUtils.appendVersionLocalChangesWarningLayout(content, lessonService, lesson);
        content.appendLayout(c -> headingWithActionLayout.render("#" + lesson.getId() + ": " + lesson.getSlug(), new InternalLink(Messages.get("lesson.enter"), routes.LessonController.enterLesson(lesson.getId())), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                LessonControllerUtils.getLessonBreadcrumbsBuilder(lesson)
                .add(new InternalLink(Messages.get("lesson.update"), routes.LessonController.editLesson(lesson.getId())))
                .build()
        );
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - Update");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private void appendSubtabs(LazyHtml content, Lesson lesson) {
        ImmutableList.Builder<InternalLink> internalLinks = ImmutableList.builder();

        internalLinks.add(new InternalLink(Messages.get("commons.view"), routes.LessonController.viewLesson(lesson.getId())));

        if (LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            internalLinks.add(new InternalLink(Messages.get("commons.update"), routes.LessonController.editLesson(lesson.getId())));
        }

        content.appendLayout(c -> subtabLayout.render(internalLinks.build(), c));
    }
}
