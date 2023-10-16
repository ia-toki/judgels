package judgels.michael.lesson.statement;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.fs.FileInfo;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.lesson.BaseLessonResource;
import judgels.michael.resource.EditStatementForm;
import judgels.michael.resource.EditStatementView;
import judgels.michael.resource.ListFilesView;
import judgels.michael.resource.ListStatementLanguagesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.StatementUtils;
import judgels.sandalphon.resource.WorldLanguageRegistry;
import judgels.service.ServiceUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/lessons/{lessonId}/statements")
public class LessonStatementResource extends BaseLessonResource {
    @Inject public LessonStatementResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewStatement(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canView(actor, lesson));

        Set<String> enabledLanguages = statementStore.getEnabledLanguages(actor.getUserJid(), lesson.getJid());
        String language = resolveStatementLanguage(req, actor, lesson, enabledLanguages);
        LessonStatement statement = statementStore.getStatement(actor.getUserJid(), lesson.getJid(), language);

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("view");
        return new ViewStatementView(template, statement, language, enabledLanguages);
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editStatement(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canView(actor, lesson));

        Set<String> enabledLanguages = statementStore.getEnabledLanguages(actor.getUserJid(), lesson.getJid());
        String language = resolveStatementLanguage(req, actor, lesson, enabledLanguages);
        LessonStatement statement = statementStore.getStatement(actor.getUserJid(), lesson.getJid(), language);

        EditStatementForm form = new EditStatementForm();
        form.title = statement.getTitle();
        form.text = statement.getText();

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, form, language, enabledLanguages, roleChecker.canEdit(actor, lesson));
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response updateStatement(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @BeanParam EditStatementForm form) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        Set<String> enabledLanguages = statementStore.getEnabledLanguages(actor.getUserJid(), lesson.getJid());
        String language = resolveStatementLanguage(req, actor, lesson, enabledLanguages);

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        statementStore.updateStatement(actor.getUserJid(), lesson.getJid(), language, new LessonStatement.Builder()
                .title(form.title)
                .text(StatementUtils.convertUnicodeToHtmlEntities(form.text))
                .build());

        return redirect("/lessons/" + lessonId + "/statements");
    }

    @GET
    @Path("/media")
    @UnitOfWork(readOnly = true)
    public View listStatementMediaFiles(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canView(actor, lesson));

        List<FileInfo> mediaFiles = statementStore.getStatementMediaFiles(actor.getUserJid(), lesson.getJid());

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("media");
        return new ListFilesView(template, req.getRequestURI(), mediaFiles, roleChecker.canEdit(actor, lesson));
    }

    @POST
    @Path("/media")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response uploadStatementMediaFiles(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            @FormDataParam("fileZipped") InputStream fileZippedStream) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        if (fileStream != null) {
            lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
            statementStore.uploadStatementMediaFile(actor.getUserJid(), lesson.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
            statementStore.uploadStatementMediaFileZipped(actor.getUserJid(), lesson.getJid(), fileZippedStream);
        }

        return redirect("/lessons/" + lessonId + "/statements/media");
    }

    @GET
    @Path("/media/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadStatementMediaFile(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @PathParam("filename") String filename) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canView(actor, lesson));

        String mediaUrl = statementStore.getStatementMediaFileURL(actor.getUserJid(), lesson.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(mediaUrl);
    }

    @GET
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public View listStatementLanguages(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canView(actor, lesson));

        Map<String, StatementLanguageStatus>
                availableLanguages = statementStore.getAvailableLanguages(actor.getUserJid(), lesson.getJid());
        String defaultLanguage = statementStore.getDefaultLanguage(actor.getUserJid(), lesson.getJid());

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("languages");
        return new ListStatementLanguagesView(template, availableLanguages, defaultLanguage, roleChecker.canEdit(actor, lesson));
    }

    @POST
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public Response addStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @FormParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        statementStore.addLanguage(actor.getUserJid(), lesson.getJid(), language);

        return redirect("/lessons/" + lessonId + "/statements/languages");
    }

    @GET
    @Path("/languages/{language}/enable")
    @UnitOfWork(readOnly = true)
    public Response enableStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        statementStore.enableLanguage(actor.getUserJid(), lesson.getJid(), language);

        return redirect("/lessons/" + lessonId + "/statements/languages");
    }

    @GET
    @Path("/languages/{language}/disable")
    @UnitOfWork(readOnly = true)
    public Response disableStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        statementStore.disableLanguage(actor.getUserJid(), lesson.getJid(), language);

        return redirect("/lessons/" + lessonId + "/statements/languages");
    }

    @GET
    @Path("/languages/{language}/makeDefault")
    @UnitOfWork(readOnly = true)
    public Response makeStatementLanguageDefault(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        statementStore.makeDefaultLanguage(actor.getUserJid(), lesson.getJid(), language);

        return redirect("/lessons/" + lessonId + "/statements/languages");
    }

    private HtmlTemplate newLessonStatementTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = newLessonTemplate(actor, lesson);
        template.setActiveMainTab("statements");
        template.addSecondaryTab("view", "View", "/lessons/" + lesson.getId() + "/statements");
        if (roleChecker.canEdit(actor, lesson)) {
            template.addSecondaryTab("edit", "Edit", "/lessons/" + lesson.getId() + "/statements/edit");
        } else {
            template.addSecondaryTab("edit", "Source", "/lessons/" + lesson.getId() + "/statements/edit");
        }
        template.addSecondaryTab("media", "Media", "/lessons/" + lesson.getId() + "/statements/media");
        template.addSecondaryTab("languages", "Languages", "/lessons/" + lesson.getId() + "/statements/languages");
        return template;
    }
}
