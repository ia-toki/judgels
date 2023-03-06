package judgels.michael.lesson.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.lesson.BaseLessonResource;
import judgels.michael.resource.EditStatementForm;
import judgels.michael.resource.EditStatementView;
import judgels.michael.resource.ListStatementLanguagesView;
import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.resource.StatementLanguageStatus;

@Path("/lessons/{lessonId}/statements")
public class LessonStatementResource extends BaseLessonResource {
    @Inject public LessonStatementResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewStatement(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canView(actor, lesson));

        Set<String> enabledLanguages = lessonStore.getEnabledLanguages(actor.getUserJid(), lesson.getJid());
        String language = resolveStatementLanguage(req, actor, lesson, enabledLanguages);
        LessonStatement statement = lessonStore.getStatement(actor.getUserJid(), lesson.getJid(), language);

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("view");
        return new ViewStatementView(template, lesson, statement, language, enabledLanguages);
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editStatement(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canView(actor, lesson));

        Set<String> enabledLanguages = lessonStore.getEnabledLanguages(actor.getUserJid(), lesson.getJid());
        String language = resolveStatementLanguage(req, actor, lesson, enabledLanguages);
        LessonStatement statement = lessonStore.getStatement(actor.getUserJid(), lesson.getJid(), language);

        EditStatementForm form = new EditStatementForm();
        form.title = statement.getTitle();
        form.text = statement.getText();

        return renderEditStatement(actor, lesson, form, language, enabledLanguages);
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response postEditStatement(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @BeanParam EditStatementForm form) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        Set<String> enabledLanguages = lessonStore.getEnabledLanguages(actor.getUserJid(), lesson.getJid());
        String language = resolveStatementLanguage(req, actor, lesson, enabledLanguages);

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        lessonStore.updateStatement(actor.getUserJid(), lesson.getJid(), language, new LessonStatement.Builder()
                .title(form.title)
                .text(form.text)
                .build());

        return Response
                .seeOther(URI.create("/lessons/" + lesson.getId() + "/statements"))
                .build();
    }

    @GET
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public View listStatementLanguages(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        Map<String, StatementLanguageStatus>
                availableLanguages = lessonStore.getAvailableLanguages(actor.getUserJid(), lesson.getJid());
        String defaultLanguage = lessonStore.getDefaultLanguage(actor.getUserJid(), lesson.getJid());

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("languages");
        return new ListStatementLanguagesView(template, availableLanguages, defaultLanguage);
    }

    private View renderEditStatement(Actor actor, Lesson lesson, HtmlForm form, String language, Set<String> enabledLanguages) {
        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, (EditStatementForm) form, language, enabledLanguages);
    }
}
