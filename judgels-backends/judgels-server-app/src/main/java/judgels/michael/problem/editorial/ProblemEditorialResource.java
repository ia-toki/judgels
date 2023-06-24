package judgels.michael.problem.editorial;

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
import judgels.michael.problem.BaseProblemResource;
import judgels.michael.resource.EditStatementForm;
import judgels.michael.resource.EditStatementView;
import judgels.michael.resource.ListFilesView;
import judgels.michael.resource.ListStatementLanguagesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemEditorial;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.WorldLanguageRegistry;
import judgels.service.ServiceUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/problems/{problemId}/editorials")
public class ProblemEditorialResource extends BaseProblemResource {
    @Inject public ProblemEditorialResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewEditorial(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        if (!editorialStore.hasEditorial(actor.getUserJid(), problem.getJid())) {
            NewEditorialForm form = new NewEditorialForm();
            form.initialLanguage = "en-US";

            HtmlTemplate template = newProblemEditorialTemplate(actor, problem, false);
            return new NewEditorialView(template, form, roleChecker.canEdit(actor, problem));
        }

        Set<String> enabledLanguages = editorialStore.getEditorialEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveEditorialLanguage(req, actor, problem, enabledLanguages);
        ProblemEditorial editorial = editorialStore.getEditorial(actor.getUserJid(), problem.getJid(), language);

        HtmlTemplate template = newProblemEditorialTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ViewEditorialView(template, editorial, language, enabledLanguages);
    }

    @POST
    @UnitOfWork
    public Response createEditorial(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam NewEditorialForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        editorialStore.initEditorials(actor.getUserJid(), problem.getJid(), form.initialLanguage);

        setCurrentStatementLanguage(req, form.initialLanguage);
        return redirect("/problems/" + problemId + "/editorials/edit");
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editEditorial(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Set<String> enabledLanguages = editorialStore.getEditorialEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveEditorialLanguage(req, actor, problem, enabledLanguages);
        ProblemEditorial editorial = editorialStore.getEditorial(actor.getUserJid(), problem.getJid(), language);

        EditStatementForm form = new EditStatementForm();
        form.text = editorial.getText();

        HtmlTemplate template = newProblemEditorialTemplate(actor, problem);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, form, language, enabledLanguages, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response updateEditorial(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditStatementForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = editorialStore.getEditorialEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveEditorialLanguage(req, actor, problem, enabledLanguages);

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        editorialStore.updateEditorial(actor.getUserJid(), problem.getJid(), language, new ProblemEditorial.Builder()
                .text(form.text)
                .build());

        return redirect("/problems/" + problemId + "/editorials");
    }

    @GET
    @Path("/media")
    @UnitOfWork(readOnly = true)
    public View listEditorialMediaFiles(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        List<FileInfo> mediaFiles = editorialStore.getEditorialMediaFiles(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemEditorialTemplate(actor, problem);
        template.setActiveSecondaryTab("media");
        return new ListFilesView(template, req.getRequestURI(), mediaFiles, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/media")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response uploadEditorialMediaFiles(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            @FormDataParam("fileZipped") InputStream fileZippedStream) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (fileStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            editorialStore.uploadEditorialMediaFile(actor.getUserJid(), problem.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            editorialStore.uploadEditorialMediaFileZipped(actor.getUserJid(), problem.getJid(), fileZippedStream);
        }

        return redirect("/problems/" + problemId + "/editorials/media");
    }

    @GET
    @Path("/media/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadEditorialMediaFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("filename") String filename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String mediaUrl = editorialStore.getEditorialMediaFileURL(actor.getUserJid(), problem.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(mediaUrl);
    }

    @GET
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public View listEditorialLanguages(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Map<String, StatementLanguageStatus>
                availableLanguages = editorialStore.getEditorialAvailableLanguages(actor.getUserJid(), problem.getJid());
        String defaultLanguage = editorialStore.getEditorialDefaultLanguage(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemEditorialTemplate(actor, problem);
        template.setActiveSecondaryTab("languages");
        return new ListStatementLanguagesView(template, availableLanguages, defaultLanguage, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public Response addEditorialLanguage(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        editorialStore.addEditorialLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/editorials/languages");
    }

    @GET
    @Path("/languages/{language}/enable")
    @UnitOfWork(readOnly = true)
    public Response enableEditorialLanguage(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        editorialStore.enableEditorialLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/editorials/languages");
    }

    @GET
    @Path("/languages/{language}/disable")
    @UnitOfWork(readOnly = true)
    public Response disableEditorialLanguage(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        editorialStore.disableEditorialLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/editorials/languages");
    }

    @GET
    @Path("/languages/{language}/makeDefault")
    @UnitOfWork(readOnly = true)
    public Response makeEditorialLanguageDefault(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        editorialStore.makeEditorialDefaultLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/editorials/languages");
    }
}
