package judgels.michael.problem.statement;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.BaseProblemResource;
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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Set<String> enabledLanguages = statementStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);
        ProblemStatement statement = statementStore.getStatement(actor.getUserJid(), problem.getJid(), language);

        EditStatementForm form = new EditStatementForm();
        form.title = statement.getTitle();
        form.text = statement.getText();

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, form, language, enabledLanguages, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response updateStatement(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditStatementForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = statementStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        statementStore.updateStatement(actor.getUserJid(), problem.getJid(), language, new ProblemStatement.Builder()
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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        List<FileInfo> mediaFiles = statementStore.getStatementMediaFiles(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("media");
        return new ListFilesView(template, req.getRequestURI(), mediaFiles, roleChecker.canEdit(actor, problem));
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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (fileStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            statementStore.uploadStatementMediaFile(actor.getUserJid(), problem.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            statementStore.uploadStatementMediaFileZipped(actor.getUserJid(), problem.getJid(), fileZippedStream);
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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String mediaUrl = statementStore.getStatementMediaFileURL(actor.getUserJid(), problem.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(mediaUrl);
    }

    @GET
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public View listStatementLanguages(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Map<String, StatementLanguageStatus> availableLanguages = statementStore.getStatementAvailableLanguages(actor.getUserJid(), problem.getJid());
        String defaultLanguage = statementStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("languages");
        return new ListStatementLanguagesView(template, availableLanguages, defaultLanguage, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public Response addStatementLanguage(
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
        statementStore.addStatementLanguage(actor.getUserJid(), problem.getJid(), language);

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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        statementStore.enableStatementLanguage(actor.getUserJid(), problem.getJid(), language);

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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        statementStore.disableStatementLanguage(actor.getUserJid(), problem.getJid(), language);

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
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return badRequest();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        statementStore.makeStatementDefaultLanguage(actor.getUserJid(), problem.getJid(), language);

        return redirect("/problems/" + problemId + "/statements/languages");
    }
}
