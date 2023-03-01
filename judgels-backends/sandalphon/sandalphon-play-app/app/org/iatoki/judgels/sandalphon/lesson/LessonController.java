package org.iatoki.judgels.sandalphon.lesson;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

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
import judgels.sandalphon.lesson.LessonStore;
import judgels.sandalphon.lesson.statement.LessonStatementUtils;
import judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.lesson.html.createLessonView;
import org.iatoki.judgels.sandalphon.lesson.html.editLessonView;
import org.iatoki.judgels.sandalphon.lesson.html.listLessonsView;
import org.iatoki.judgels.sandalphon.lesson.html.viewLessonView;
import org.iatoki.judgels.sandalphon.role.RoleChecker;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class LessonController extends AbstractLessonController {
    private final RoleChecker roleChecker;
    private final LessonStore lessonStore;
    private final LessonRoleChecker lessonRoleChecker;
    private final ProfileService profileService;

    @Inject
    public LessonController(
            RoleChecker roleChecker,
            LessonStore lessonStore,
            LessonRoleChecker lessonRoleChecker,
            ProfileService profileService) {

        super(lessonStore, lessonRoleChecker);
        this.roleChecker = roleChecker;
        this.lessonStore = lessonStore;
        this.lessonRoleChecker = lessonRoleChecker;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result index(Http.Request req) {
        return listLessons(req, 1, "updatedAt", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listLessons(Http.Request req, long pageIndex, String sortBy, String orderBy, String filterString) {
        String actorJid = getUserJid(req);

        boolean isAdmin = roleChecker.isAdmin(req);
        boolean isWriter = roleChecker.isWriter(req);
        Page<Lesson> pageOfLessons = lessonStore.getPageOfLessons(pageIndex, sortBy, orderBy, filterString, actorJid, isAdmin);

        Set<String> userJids = pageOfLessons.getPage().stream().map(Lesson::getAuthorJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listLessonsView.render(pageOfLessons, profilesMap, sortBy, orderBy, filterString));
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

        if (lessonStore.lessonExistsBySlug(lessonCreateForm.get().slug)) {
            return showCreateLesson(req, lessonCreateForm.withError("slug", "Slug already exists"));
        }

        LessonCreateForm lessonCreateData = lessonCreateForm.get();

        Lesson lesson = lessonStore.createLesson(lessonCreateData.slug, lessonCreateData.additionalNote, lessonCreateData.initLanguageCode);
        lessonStore.updateStatement(null, lesson.getJid(), lessonCreateData.initLanguageCode, new LessonStatement.Builder()
                .title(ProblemStatementUtils.getDefaultTitle(lessonCreateData.initLanguageCode))
                .text(LessonStatementUtils.getDefaultText(lessonCreateData.initLanguageCode))
                .build());

        lessonStore.initRepository(actorJid, lesson.getJid());

        return redirect(routes.LessonController.enterLesson(lesson.getId()))
                .addingToSession(req, newCurrentStatementLanguage(lessonCreateData.initLanguageCode));
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
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));

        Profile profile = profileService.getProfile(lesson.getAuthorJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(viewLessonView.render(lesson, profile));
        template.setMainTitle("#" + lesson.getId() + ": " + lesson.getSlug());
        template.markBreadcrumbLocation("View lesson", routes.LessonController.viewLesson(lesson.getId()));
        template.setPageTitle("Lesson - View");

        return renderLessonTemplate(template, lesson);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editLesson(Http.Request req, long lessonId) {
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToUpdateLesson(req, lesson));

        LessonEditForm lessonEditData = new LessonEditForm();
        lessonEditData.slug = lesson.getSlug();
        lessonEditData.additionalNote = lesson.getAdditionalNote();

        Form<LessonEditForm> lessonEditForm = formFactory.form(LessonEditForm.class).fill(lessonEditData);

        return showEditLesson(req, lessonEditForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditLesson(Http.Request req, long lessonId) {
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToUpdateLesson(req, lesson));

        Form<LessonEditForm> lessonEditForm = formFactory.form(LessonEditForm.class).bindFromRequest(req);

        if (formHasErrors(lessonEditForm)) {
            return showEditLesson(req, lessonEditForm, lesson);
        }

        if (!lesson.getSlug().equals(lessonEditForm.get().slug) && lessonStore.lessonExistsBySlug(lessonEditForm.get().slug)) {
            return showEditLesson(req, lessonEditForm.withError("slug", "Slug already exists"), lesson);
        }

        LessonEditForm lessonEditData = lessonEditForm.get();
        lessonStore.updateLesson(lesson.getJid(), lessonEditData.slug, lessonEditData.additionalNote);

        return redirect(routes.LessonController.viewLesson(lesson.getId()));
    }

    @RequireCSRFCheck
    public Result switchLanguage(Http.Request req, long lessonId) {
        String language = formFactory.form().bindFromRequest(req).get("langCode");

        return redirect(req.getHeaders().get("Referer").orElse(""))
                .addingToSession(req, newCurrentStatementLanguage(language));
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
        template.markBreadcrumbLocation("Update lesson", routes.LessonController.editLesson(lesson.getId()));
        template.setPageTitle("Lesson - Update");

        return renderLessonTemplate(template, lesson);
    }

    protected Result renderLessonTemplate(HtmlTemplate template, Lesson lesson) {
        template.addSecondaryTab("View", routes.LessonController.viewLesson(lesson.getId()));

        if (lessonRoleChecker.isAllowedToUpdateLesson(template.getRequest(), lesson)) {
            template.addSecondaryTab("Update", routes.LessonController.editLesson(lesson.getId()));
        }

        return super.renderTemplate(template, lesson);
    }
}
