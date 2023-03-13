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
import judgels.sandalphon.resource.WorldLanguageRegistry;
import judgels.service.ServiceUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
        return new ViewStatementView(template, statement, language, enabledLanguages);
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

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, form, "/lessons", language, enabledLanguages);
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

        return redirect("/lessons/" + lessonId + "/statements");
    }

    @GET
    @Path("/media")
    @UnitOfWork(readOnly = true)
    public View listStatementMediaFiles(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        List<FileInfo> mediaFiles = lessonStore.getStatementMediaFiles(actor.getUserJid(), lesson.getJid());

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("media");
        return new ListFilesView(template, req.getRequestURI(), mediaFiles);
    }

    @POST
    @Path("/media")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response postUploadStatementMediaFiles(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            @FormDataParam("fileZipped") InputStream fileZippedStream) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        if (fileStream != null) {
            lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
            lessonStore.uploadStatementMediaFile(actor.getUserJid(), lesson.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
            lessonStore.uploadStatementMediaFileZipped(actor.getUserJid(), lesson.getJid(), fileZippedStream);
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
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        String mediaUrl = lessonStore.getStatementMediaFileURL(actor.getUserJid(), lesson.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(mediaUrl);
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
        return new ListStatementLanguagesView(template, req.getRequestURI(), availableLanguages, defaultLanguage);
    }

    @POST
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public Response postAddStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @FormParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        lessonStore.addLanguage(actor.getUserJid(), lesson.getJid(), language);

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
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        lessonStore.enableLanguage(actor.getUserJid(), lesson.getJid(), language);

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
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        lessonStore.disableLanguage(actor.getUserJid(), lesson.getJid(), language);

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
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canEdit(actor, lesson));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        lessonStore.createUserCloneIfNotExists(actor.getUserJid(), lesson.getJid());
        lessonStore.makeDefaultLanguage(actor.getUserJid(), lesson.getJid(), language);

        return redirect("/lessons/" + lessonId + "/statements/languages");
    }
}
