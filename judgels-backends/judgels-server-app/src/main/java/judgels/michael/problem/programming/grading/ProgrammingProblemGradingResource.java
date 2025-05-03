package judgels.michael.problem.programming.grading;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.programming.BaseProgrammingProblemResource;
import judgels.michael.problem.programming.grading.config.EditGradingConfigView;
import judgels.michael.problem.programming.grading.config.GradingConfigAdapter;
import judgels.michael.problem.programming.grading.config.GradingConfigAdapterRegistry;
import judgels.michael.problem.programming.grading.config.GradingConfigForm;
import judgels.michael.resource.ListFilesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.service.ServiceUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/problems/programming/{problemId}/grading")
public class ProgrammingProblemGradingResource extends BaseProgrammingProblemResource {
    @Inject public ProgrammingProblemGradingResource() {}

    @GET
    @Path("/engine")
    @UnitOfWork(readOnly = true)
    public View editGradingEngine(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        EditGradingEngineForm form = new EditGradingEngineForm();
        form.gradingEngine = programmingProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());

        return renderEditGradingEngine(actor, problem, form, roleChecker.canEdit(actor, problem));
    }

    private View renderEditGradingEngine(Actor actor, Problem problem, EditGradingEngineForm form, boolean canEdit) {
        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("engine");
        return new EditGradingEngineView(template, form, canEdit);
    }

    @POST
    @Path("/engine")
    @UnitOfWork
    public Response updateGradingEngine(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditGradingEngineForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        String engine = form.gradingEngine;
        String oldEngine = programmingProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());

        if (!engine.equals(oldEngine)) {
            GradingConfig config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
            programmingProblemStore.updateGradingConfig(actor.getUserJid(), problem.getJid(), config);
        }

        programmingProblemStore.updateGradingEngine(actor.getUserJid(), problem.getJid(), engine);

        return redirect("/problems/programming/" + problemId + "/grading/config");
    }

    @GET
    @Path("/config")
    @UnitOfWork(readOnly = true)
    public View editGradingConfig(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String engine = programmingProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());
        GradingConfig config  = programmingProblemStore.getGradingConfig(actor.getUserJid(), problem.getJid());
        List<FileInfo> testDataFiles = programmingProblemStore.getGradingTestDataFiles(actor.getUserJid(), problem.getJid());
        List<FileInfo> helperFiles = programmingProblemStore.getGradingHelperFiles(actor.getUserJid(), problem.getJid());

        GradingConfigAdapter adapter = GradingConfigAdapterRegistry.getInstance().get(engine);
        GradingConfigForm form = adapter.buildFormFromConfig(config);

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("config");
        return new EditGradingConfigView(engine, template, form, testDataFiles, helperFiles, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/config")
    @UnitOfWork
    public Response updateGradingConfig(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam GradingConfigForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        String engine = programmingProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        GradingConfigAdapter adapter = GradingConfigAdapterRegistry.getInstance().get(engine);
        GradingConfig config = adapter.buildConfigFromForm(form);

        programmingProblemStore.updateGradingConfig(actor.getUserJid(), problem.getJid(), config);

        return redirect("/problems/programming/" + problemId + "/grading/config");
    }

    @GET
    @Path("/config/auto-populate")
    @UnitOfWork
    public Response autoPopulateGradingConfigTestData(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        String engine = programmingProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());
        GradingConfig config  = programmingProblemStore.getGradingConfig(actor.getUserJid(), problem.getJid());
        List<FileInfo> testDataFiles = programmingProblemStore.getGradingTestDataFiles(actor.getUserJid(), problem.getJid());

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        GradingConfigAdapter adapter = GradingConfigAdapterRegistry.getInstance().get(engine);
        GradingConfig newConfig = adapter.autoPopulateTestData(config, testDataFiles);

        programmingProblemStore.updateGradingConfig(actor.getUserJid(), problem.getJid(), newConfig);

        return redirect("/problems/programming/" + problemId + "/grading/config");
    }

    @GET
    @Path("/testdata")
    @UnitOfWork(readOnly = true)
    public View listGradingTestDataFiles(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        List<FileInfo> testDataFiles = programmingProblemStore.getGradingTestDataFiles(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("testdata");
        return new ListFilesView(template, req.getRequestURI(), testDataFiles, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/testdata")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response uploadGradingTestDataFiles(
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
            programmingProblemStore.uploadGradingTestDataFile(actor.getUserJid(), problem.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            programmingProblemStore.uploadGradingTestDataFileZipped(actor.getUserJid(), problem.getJid(), fileZippedStream);
        }

        return redirect("/problems/programming/" + problemId + "/grading/testdata");
    }

    @GET
    @Path("/testdata/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadGradingTestDataFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("filename") String filename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String testDataFileUrl = programmingProblemStore.getGradingTestDataFileURL(actor.getUserJid(), problem.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(testDataFileUrl);
    }

    @GET
    @Path("/helpers")
    @UnitOfWork(readOnly = true)
    public View listGradingHelperFiles(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        List<FileInfo> helperFiles = programmingProblemStore.getGradingHelperFiles(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("helpers");
        return new ListFilesView(template, req.getRequestURI(), helperFiles, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/helpers")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response uploadGradingHelperFiles(
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
            programmingProblemStore.uploadGradingHelperFile(actor.getUserJid(), problem.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            programmingProblemStore.uploadGradingHelperFileZipped(actor.getUserJid(), problem.getJid(), fileZippedStream);
        }

        return redirect("/problems/programming/" + problemId + "/grading/helpers");
    }

    @GET
    @Path("/helpers/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadGradingHelperFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("filename") String filename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String helperFileUrl = programmingProblemStore.getGradingHelperFileURL(actor.getUserJid(), problem.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(helperFileUrl);
    }

    @GET
    @Path("/languageRestriction")
    @UnitOfWork(readOnly = true)
    public View editGradingLanguageRestriction(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        LanguageRestriction languageRestriction = programmingProblemStore.getLanguageRestriction(actor.getUserJid(), problem.getJid());

        EditGradingLanguageRestrictionForm form = new EditGradingLanguageRestrictionForm();
        form.isAllowedAll = languageRestriction.isAllowedAll();
        form.allowedLanguages = languageRestriction.getAllowedLanguages();

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("languageRestriction");
        return new EditGradingLanguageRestrictionView(template, form, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/languageRestriction")
    @UnitOfWork
    public Response updateGradingLanguageRestriction(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditGradingLanguageRestrictionForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        LanguageRestriction languageRestriction = LanguageRestrictionAdapter.getLanguageRestriction(form.isAllowedAll, form.allowedLanguages);
        programmingProblemStore.updateLanguageRestriction(actor.getUserJid(), problem.getJid(), languageRestriction);

        return redirect("/problems/programming/" + problemId + "/grading/languageRestriction");
    }

    protected HtmlTemplate newProblemGradingTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("grading");
        template.addSecondaryTab("engine", "Engine", "/problems/programming/" + problem.getId() + "/grading/engine");
        template.addSecondaryTab("config", "Config", "/problems/programming/" + problem.getId() + "/grading/config");
        template.addSecondaryTab("testdata", "Test data", "/problems/programming/" + problem.getId() + "/grading/testdata");
        template.addSecondaryTab("helpers", "Helpers", "/problems/programming/" + problem.getId() + "/grading/helpers");
        template.addSecondaryTab("languageRestriction", "Language restriction", "/problems/programming/" + problem.getId() + "/grading/languageRestriction");
        return template;
    }
}
