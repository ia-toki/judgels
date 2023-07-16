package judgels.michael.lesson;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.Map;
import java.util.Optional;
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
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.SearchLessonsWidget;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.lesson.Lesson;

@Path("/lessons")
public class LessonResource extends BaseLessonResource {
    private static final int PAGE_SIZE = 20;

    @Inject public LessonResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(
            @Context HttpServletRequest req,
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("term") @DefaultValue("") String termFilter) {

        Actor actor = actorChecker.check(req);
        boolean isAdmin = roleChecker.isAdmin(actor);
        boolean isWriter = roleChecker.isWriter(actor);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actor.getUserJid());
        Page<Lesson> lessons = lessonStore.getLessons(userJid, termFilter, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(lessons.getPage(), Lesson::getAuthorJid);
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        HtmlTemplate template = newLessonsTemplate(actor);
        template.setTitle("Lessons");
        if (isWriter) {
            template.addMainButton("New lesson", "/lessons/new");
        }
        template.setSearchLessonsWidget(new SearchLessonsWidget(pageNumber, termFilter));
        return new ListLessonsView(template, lessons, termFilter, profilesMap);
    }

    @GET
    @Path("/new")
    @UnitOfWork(readOnly = true)
    public View newLesson(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        NewLessonForm form = new NewLessonForm();
        form.initialLanguage = "en-US";

        return renderNewLesson(actor, form);
    }

    public View renderNewLesson(Actor actor, NewLessonForm form) {
        HtmlTemplate template = newLessonsTemplate(actor);
        template.setTitle("New lesson");
        return new NewLessonView(template, form);
    }

    @POST
    @Path("/new")
    @UnitOfWork
    public Response createLesson(@Context HttpServletRequest req, @BeanParam NewLessonForm form) {
        Actor actor = actorChecker.check(req);
        checkAllowed(roleChecker.isWriter(actor));

        if (lessonStore.lessonExistsBySlug(form.slug)) {
            form.globalError = "Slug already exists.";
            return ok(renderNewLesson(actor, form));
        }

        Lesson lesson = lessonStore.createLesson(form.slug, form.additionalNote);

        statementStore.initStatements(lesson.getJid(), form.initialLanguage);

        lessonStore.initRepository(actor.getUserJid(), lesson.getJid());

        setCurrentStatementLanguage(req, form.initialLanguage);
        return redirect("/lessons/" + lesson.getId() + "/statements");
    }

    @GET
    @Path("/{lessonId}")
    @UnitOfWork(readOnly = true)
    public View viewLesson(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canView(actor, lesson));

        Profile profile = profileStore.getProfile(lesson.getAuthorJid());

        HtmlTemplate template = newLessonGeneralTemplate(actor, lesson);
        template.setActiveSecondaryTab("view");
        return new ViewLessonView(template, lesson, profile);
    }

    @GET
    @Path("/{lessonId}/edit")
    @UnitOfWork(readOnly = true)
    public View editLesson(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        EditLessonForm form = new EditLessonForm();
        form.slug = lesson.getSlug();
        form.additionalNote = lesson.getAdditionalNote();

        return renderEditLesson(actor, lesson, form);
    }

    private View renderEditLesson(Actor actor, Lesson lesson, EditLessonForm form) {
        HtmlTemplate template = newLessonGeneralTemplate(actor, lesson);
        template.setActiveSecondaryTab("edit");
        return new EditLessonView(template, form);
    }

    @POST
    @Path("/{lessonId}/edit")
    @UnitOfWork
    public Response updateLesson(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @BeanParam EditLessonForm form) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        if (!lesson.getSlug().equals(form.slug) && lessonStore.lessonExistsBySlug(form.slug)) {
            form.globalError = "Slug already exists.";
            return ok(renderEditLesson(actor, lesson, form));
        }

        lessonStore.updateLesson(lesson.getJid(), form.slug, form.additionalNote);

        return redirect("/lessons/" + lessonId);
    }

    private HtmlTemplate newLessonGeneralTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = newLessonTemplate(actor, lesson);
        template.setActiveMainTab("general");
        template.addSecondaryTab("view", "View", "/lessons/" + lesson.getId());
        if (roleChecker.canEdit(actor, lesson)) {
            template.addSecondaryTab("edit", "Edit", "/lessons/" + lesson.getId() + "/edit");
        }
        return template;
    }
}
