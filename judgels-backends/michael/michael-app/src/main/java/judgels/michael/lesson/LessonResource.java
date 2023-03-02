package judgels.michael.lesson;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.profile.ProfileStore;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.SearchLessonsWidget;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.lesson.LessonRoleChecker;
import judgels.sandalphon.lesson.LessonStore;
import judgels.sandalphon.lesson.statement.LessonStatementUtils;
import judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import judgels.sandalphon.role.RoleChecker;

@Path("/lessons")
public class LessonResource extends BaseLessonResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected LessonRoleChecker lessonRoleChecker;
    @Inject protected LessonStore lessonStore;
    @Inject protected ProfileStore profileStore;

    @Inject public LessonResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(
            @Context HttpServletRequest req,
            @QueryParam("page") @DefaultValue("1") int pageIndex,
            @QueryParam("filter") @DefaultValue("") String filterString) {

        Actor actor = actorChecker.check(req);
        boolean isAdmin = roleChecker.isAdmin(actor);
        boolean isWriter = roleChecker.isWriter(actor);

        Page<Lesson> lessons = lessonStore.getPageOfLessons(pageIndex, "updatedAt", "desc", filterString, actor.getUserJid(), isAdmin);
        Set<String> userJids = lessons.getPage().stream().map(Lesson::getAuthorJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        HtmlTemplate template = newLessonsTemplate(actor);
        template.setTitle("Lessons");
        if (isWriter) {
            template.addMainButton("Create", "/lessons/new");
        }
        template.setSearchLessonsWidget(new SearchLessonsWidget(pageIndex, filterString));
        return new ListLessonsView(template, lessons, filterString, profilesMap);
    }

    @GET
    @Path("/new")
    @UnitOfWork(readOnly = true)
    public View createLesson(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        CreateLessonForm form = new CreateLessonForm();
        form.initialLanguage = "en-US";

        return renderCreateLesson(actor, form);
    }

    @POST
    @Path("/new")
    @UnitOfWork
    public Response postCreateLesson(@Context HttpServletRequest req, @BeanParam CreateLessonForm form) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        if (lessonStore.lessonExistsBySlug(form.slug)) {
            return Response.ok(renderCreateLesson(actor, form.withGlobalError("Slug already exists."))).build();
        }

        Lesson lesson = lessonStore.createLesson(form.slug, form.additionalNote, form.initialLanguage);
        lessonStore.updateStatement(null, lesson.getJid(), form.initialLanguage, new LessonStatement.Builder()
                .title(ProblemStatementUtils.getDefaultTitle(form.initialLanguage))
                .text(LessonStatementUtils.getDefaultText(form.initialLanguage))
                .build());

        lessonStore.initRepository(actor.getUserJid(), lesson.getJid());

        return Response
                .seeOther(URI.create("/lessons/" + lesson.getId()))
                .build();
    }

    @GET
    @Path("/{lessonId}")
    @UnitOfWork(readOnly = true)
    public View viewLesson(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canView(actor, lesson));

        Profile profile = profileStore.getProfile(Instant.now(), lesson.getAuthorJid());

        HtmlTemplate template = newLessonGeneralTemplate(actor, lesson);
        template.setActiveSecondaryTab("view");
        return new ViewLessonView(template, lesson, profile);
    }

    @GET
    @Path("/{lessonId}/edit")
    @UnitOfWork(readOnly = true)
    public View editLesson(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        EditLessonForm form = new EditLessonForm();
        form.slug = lesson.getSlug();
        form.additionalNote = lesson.getAdditionalNote();

        return renderEditLesson(actor, lesson, form);
    }

    @POST
    @Path("/{lessonId}/edit")
    @UnitOfWork
    public Response postEditLesson(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @BeanParam EditLessonForm form) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        if (!lesson.getSlug().equals(form.slug) && lessonStore.lessonExistsBySlug(form.slug)) {
            return Response.ok(renderEditLesson(actor, lesson, form.withGlobalError("Slug already exists."))).build();
        }

        lessonStore.updateLesson(lesson.getJid(), form.slug, form.additionalNote);

        return Response
                .seeOther(URI.create("/lessons/" + lesson.getId()))
                .build();
    }

    private HtmlTemplate newLessonGeneralTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = newLessonTemplate(actor, lesson);
        template.setActiveMainTab("general");
        template.addSecondaryTab("view", "View", "/lessons/" + lesson.getId());
        template.addSecondaryTab("edit", "Edit", "/lessons/" + lesson.getId() + "/edit");
        return template;
    }

    private View renderCreateLesson(Actor actor, HtmlForm form) {
        HtmlTemplate template = newLessonsTemplate(actor);
        template.setTitle("New lesson");
        return new CreateLessonView(template, (CreateLessonForm) form);
    }

    private View renderEditLesson(Actor actor, Lesson lesson, HtmlForm form) {
        HtmlTemplate template = newLessonGeneralTemplate(actor, lesson);
        template.setActiveSecondaryTab("edit");
        return new EditLessonView(template, (EditLessonForm) form);
    }
}
