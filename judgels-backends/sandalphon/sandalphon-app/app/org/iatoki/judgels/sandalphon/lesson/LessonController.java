package org.iatoki.judgels.sandalphon.lesson;

import static judgels.service.ServiceUtils.checkFound;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.SandalphonSessionUtils;
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
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class LessonController extends AbstractLessonController {
    private static final long PAGE_SIZE = 20;

    private final LessonService lessonService;
    private final ProfileService profileService;

    @Inject
    public LessonController(LessonService lessonService, ProfileService profileService) {
        super(lessonService);
        this.lessonService = lessonService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result index(Http.Request req) {
        return listLessons(req, 0, "updatedAt", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listLessons(Http.Request req, long pageIndex, String sortBy, String orderBy, String filterString) {
        String actorJid = getUserJid(req);

        boolean isAdmin = SandalphonControllerUtils.getInstance().isAdmin();
        boolean isWriter = SandalphonControllerUtils.getInstance().isWriter();
        Page<Lesson> pageOfLessons = lessonService.getPageOfLessons(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString, actorJid, isAdmin);

        Set<String> userJids = pageOfLessons.getPage().stream().map(Lesson::getAuthorJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listLessonsView.render(pageOfLessons, profilesMap, sortBy, orderBy, filterString, isWriter));
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
    public Result createLesson(Http.Request req) {
        Form<LessonCreateForm> lessonCreateForm = formFactory.form(LessonCreateForm.class);

        return showCreateLesson(req, lessonCreateForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateLesson(Http.Request req) {
        String actorJid = getUserJid(req);

        Form<LessonCreateForm> lessonCreateForm = formFactory.form(LessonCreateForm.class).bindFromRequest(req);

        if (formHasErrors(lessonCreateForm)) {
            return showCreateLesson(req, lessonCreateForm);
        }

        if (lessonService.lessonExistsBySlug(lessonCreateForm.get().slug)) {
            return showCreateLesson(req, lessonCreateForm.withError("slug", "Slug already exists"));
        }

        LessonCreateForm lessonCreateData = lessonCreateForm.get();

        Lesson lesson;
        try {
            lesson = lessonService.createLesson(lessonCreateData.slug, lessonCreateData.additionalNote, lessonCreateData.initLanguageCode);
            lessonService.updateStatement(null, lesson.getJid(), lessonCreateData.initLanguageCode, new LessonStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(lessonCreateData.initLanguageCode))
                    .text(LessonStatementUtils.getDefaultText(lessonCreateData.initLanguageCode))
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lessonService.initRepository(actorJid, lesson.getJid());

        return redirect(routes.LessonController.index())
                .addingToSession(req, SandalphonSessionUtils.newCurrentStatementLanguage(lessonCreateData.initLanguageCode));
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
    public Result viewLesson(Http.Request req, long lessonId) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        Profile profile = profileService.getProfile(lesson.getAuthorJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(viewLessonView.render(lesson, profile));
        template.setMainTitle("#" + lesson.getId() + ": " + lesson.getSlug());
        template.addMainButton("Enter lesson", routes.LessonController.enterLesson(lesson.getId()));
        template.markBreadcrumbLocation("View lesson", routes.LessonController.viewLesson(lesson.getId()));
        template.setPageTitle("Lesson - View");

        return renderLessonTemplate(template, lessonService, lesson);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editLesson(Http.Request req, long lessonId) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            return redirect(routes.LessonController.viewLesson(lesson.getId()));
        }

        LessonEditForm lessonEditData = new LessonEditForm();
        lessonEditData.slug = lesson.getSlug();
        lessonEditData.additionalNote = lesson.getAdditionalNote();

        Form<LessonEditForm> lessonEditForm = formFactory.form(LessonEditForm.class).fill(lessonEditData);

        return showEditLesson(req, lessonEditForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditLesson(Http.Request req, long lessonId) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            return notFound();
        }

        Form<LessonEditForm> lessonEditForm = formFactory.form(LessonEditForm.class).bindFromRequest(req);

        if (formHasErrors(lessonEditForm)) {
            return showEditLesson(req, lessonEditForm, lesson);
        }

        if (!lesson.getSlug().equals(lessonEditForm.get().slug) && lessonService.lessonExistsBySlug(lessonEditForm.get().slug)) {
            return showEditLesson(req, lessonEditForm.withError("slug", "Slug already exists"), lesson);
        }

        LessonEditForm lessonEditData = lessonEditForm.get();
        lessonService.updateLesson(lesson.getJid(), lessonEditData.slug, lessonEditData.additionalNote);

        return redirect(routes.LessonController.viewLesson(lesson.getId()));
    }

    @RequireCSRFCheck
    public Result switchLanguage(Http.Request req, long lessonId) {
        String language = formFactory.form().bindFromRequest(req).get("langCode");

        return redirect(req.getHeaders().get("Referer").orElse(""))
                .addingToSession(req, SandalphonSessionUtils.newCurrentStatementLanguage(language));
    }

    private Result showCreateLesson(Http.Request req, Form<LessonCreateForm> lessonCreateForm) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(createLessonView.render(lessonCreateForm));
        template.setMainTitle("Create lesson");
        template.markBreadcrumbLocation("Create lesson", routes.LessonController.createLesson());
        template.setPageTitle("Lesson - Create");

        return renderTemplate(template);
    }

    private Result showEditLesson(Http.Request req, Form<LessonEditForm> lessonEditForm, Lesson lesson) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
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
