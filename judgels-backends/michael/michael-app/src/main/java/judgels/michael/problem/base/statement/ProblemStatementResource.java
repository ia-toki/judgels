package judgels.michael.problem.base.statement;

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
import judgels.michael.problem.base.BaseProblemResource;
import judgels.michael.resource.EditStatementForm;
import judgels.michael.resource.EditStatementView;
import judgels.michael.resource.ListFilesView;
import judgels.michael.resource.ListStatementLanguagesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.WorldLanguageRegistry;
import judgels.service.ServiceUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/problems/{problemId}/statements")
public class ProblemStatementResource extends BaseProblemResource {
    @Inject public ProblemStatementResource() {}

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editStatement(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = problemStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);
        ProblemStatement statement = problemStore.getStatement(actor.getUserJid(), problem.getJid(), language);

        EditStatementForm form = new EditStatementForm();
        form.title = statement.getTitle();
        form.text = statement.getText();

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, form, language, enabledLanguages);
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response updateStatement(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditStatementForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = problemStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.updateStatement(actor.getUserJid(), problem.getJid(), language, new ProblemStatement.Builder()
                .title(form.title)
                .text(form.text)
                .build());

        return redirect("/problems/" + problem.getType().name().toLowerCase() + "/" + problemId + "/statements");
    }

    @GET
    @Path("/media")
    @UnitOfWork(readOnly = true)
    public View listStatementMediaFiles(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canView(actor, problem));

        List<FileInfo> mediaFiles = problemStore.getStatementMediaFiles(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("media");
        return new ListFilesView(template, req.getRequestURI(), mediaFiles, problemRoleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/media")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response uploadStatementMediaFiles(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            @FormDataParam("fileZipped") InputStream fileZippedStream) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        if (fileStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            problemStore.uploadStatementMediaFile(actor.getUserJid(), problem.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            problemStore.uploadStatementMediaFileZipped(actor.getUserJid(), problem.getJid(), fileZippedStream);
        }

        return redirect("/problems/" + problemId + "/statements/media");
    }

    @GET
    @Path("/media/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadStatementMediaFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("filename") String filename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canView(actor, problem));

        String mediaUrl = problemStore.getStatementMediaFileURL(actor.getUserJid(), problem.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(mediaUrl);
    }

    @GET
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public View listStatementLanguages(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canView(actor, problem));

        Map<String, StatementLanguageStatus> availableLanguages = problemStore.getStatementAvailableLanguages(actor.getUserJid(), problem.getJid());
        String defaultLanguage = problemStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("languages");
        return new ListStatementLanguagesView(template, availableLanguages, defaultLanguage, problemRoleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public Response addStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.addStatementLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/statements/languages");
    }

    @GET
    @Path("/languages/{language}/enable")
    @UnitOfWork(readOnly = true)
    public Response enableStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.enableStatementLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/statements/languages");
    }

    @GET
    @Path("/languages/{language}/disable")
    @UnitOfWork(readOnly = true)
    public Response disableStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.disableStatementLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/statements/languages");
    }

    @GET
    @Path("/languages/{language}/makeDefault")
    @UnitOfWork(readOnly = true)
    public Response makeStatementLanguageDefault(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.makeStatementDefaultLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/statements/languages");
    }
}
