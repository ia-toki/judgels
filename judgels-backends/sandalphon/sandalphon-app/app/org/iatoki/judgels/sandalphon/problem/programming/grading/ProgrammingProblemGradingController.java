package org.iatoki.judgels.sandalphon.problem.programming.grading;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.grading.html.autoPopulationLayout;
import org.iatoki.judgels.sandalphon.problem.programming.grading.html.editGradingEngineView;
import org.iatoki.judgels.sandalphon.problem.programming.grading.html.editLanguageRestrictionView;
import org.iatoki.judgels.sandalphon.problem.programming.grading.html.listGradingHelperFilesView;
import org.iatoki.judgels.sandalphon.problem.programming.grading.html.listGradingTestDataFilesView;
import org.iatoki.judgels.sandalphon.problem.programming.grading.html.tokilibLayout;
import org.iatoki.judgels.sandalphon.resource.UploadFileForm;
import play.api.mvc.Call;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemGradingController extends AbstractProblemController {
    private final ProblemService problemService;
    private final ProblemRoleChecker problemRoleChecker;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ProgrammingProblemGradingController(
            ProblemService problemService,
            ProblemRoleChecker problemRoleChecker,
            ProgrammingProblemService programmingProblemService) {

        super(problemService, problemRoleChecker);
        this.problemService = problemService;
        this.problemRoleChecker = problemRoleChecker;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editGradingEngine(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        GradingEngineEditForm gradingEngineEditData = new GradingEngineEditForm();
        try  {
            gradingEngineEditData.gradingEngineName = programmingProblemService.getGradingEngine(actorJid, problem.getJid());
        } catch (IOException e) {
            gradingEngineEditData.gradingEngineName = GradingEngineRegistry.getInstance().getDefault();
        }

        Form<GradingEngineEditForm> gradingEngineEditForm = formFactory.form(GradingEngineEditForm.class).fill(gradingEngineEditData);

        return showEditGradingEngine(req, gradingEngineEditForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditGradingEngine(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        Form<GradingEngineEditForm> gradingEngineEditForm = formFactory.form(GradingEngineEditForm.class).bindFromRequest(req);

        if (formHasErrors(gradingEngineEditForm)) {
            return showEditGradingEngine(req, gradingEngineEditForm, problem);
        }

        problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

        String gradingEngine = gradingEngineEditForm.get().gradingEngineName;
        String originalGradingEngine;
        try {
            originalGradingEngine = programmingProblemService.getGradingEngine(actorJid, problem.getJid());
        } catch (IOException e) {
            originalGradingEngine = GradingEngineRegistry.getInstance().getDefault();
        }

        try {
            if (!gradingEngine.equals(originalGradingEngine)) {
                GradingConfig config = GradingEngineRegistry.getInstance().get(gradingEngine).createDefaultConfig();
                programmingProblemService.updateGradingConfig(actorJid, problem.getJid(), config);
            }

            programmingProblemService.updateGradingEngine(actorJid, problem.getJid(), gradingEngine);
        } catch (IOException e) {
            return showEditGradingEngine(req, gradingEngineEditForm.withGlobalError("Error updating grading engine."), problem);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editGradingConfig(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(actorJid, problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        GradingConfig config;
        try {
            config = programmingProblemService.getGradingConfig(actorJid, problem.getJid());
        } catch (IOException e) {
            config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
        }
        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());
        List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(actorJid, problem.getJid());

        Form<?> gradingEngineConfForm = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).createFormFromConfig(formFactory, config);

        return showEditGradingConfig(req, gradingEngineConfForm, problem, engine, testDataFiles, helperFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditGradingConfig(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(actorJid, problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        Form<?> gradingEngineConfForm = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).createEmptyForm(formFactory).bindFromRequest(req);

        if (formHasErrors(gradingEngineConfForm)) {
            List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());
            List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(actorJid, problem.getJid());

            return showEditGradingConfig(req, gradingEngineConfForm, problem, engine, testDataFiles, helperFiles);
        }

        problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

        try {
            GradingConfig config = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).createConfigFromForm(gradingEngineConfForm);
            programmingProblemService.updateGradingConfig(actorJid, problem.getJid(), config);
        } catch (IOException e) {
            List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());
            List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(actorJid, problem.getJid());

            return showEditGradingConfig(req, gradingEngineConfForm.withGlobalError("Error updating grading config."), problem, engine, testDataFiles, helperFiles);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional
    public Result editGradingConfigByTokilibFormat(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(actorJid, problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }

        GradingEngineAdapter adapter = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine);

        if (!(adapter instanceof ConfigurableWithTokilibFormat)) {
            return forbidden();
        }

        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());
        GradingConfig config;
        try {
            config = programmingProblemService.getGradingConfig(actorJid, problem.getJid());
        } catch (IOException e) {
            config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
        }

        try {
            GradingConfig newConfig = ((ConfigurableWithTokilibFormat) adapter).updateConfigWithTokilibFormat(config, testDataFiles);
            programmingProblemService.updateGradingConfig(actorJid, problem.getJid(), newConfig);
        } catch (IOException e) {
            throw new IllegalStateException("Can't update grading config using tokilib format", e);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional
    public Result editGradingConfigByAutoPopulation(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(actorJid, problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());
        GradingEngineAdapter adapter = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine);

        if (!(adapter instanceof ConfigurableWithAutoPopulation)) {
            return forbidden();
        }

        GradingConfig config;
        try {
            config = programmingProblemService.getGradingConfig(actorJid, problem.getJid());
        } catch (IOException e) {
            config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
        }
        GradingConfig newConfig = ((ConfigurableWithAutoPopulation) adapter).updateConfigWithAutoPopulation(config, testDataFiles);

        try {
            programmingProblemService.updateGradingConfig(actorJid, problem.getJid(), newConfig);
        } catch (IOException e) {
            throw new IllegalStateException("Can't update grading config using auto population", e);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listGradingTestDataFiles(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        Form<UploadFileForm> uploadFileForm = formFactory.form(UploadFileForm.class);
        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());

        return showListGradingTestDataFiles(req, uploadFileForm, problem, testDataFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadGradingTestDataFiles(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file;

        file = body.getFile("file");
        if (file != null) {
            problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

            File testDataFile = file.getFile();
            try {
                programmingProblemService.uploadGradingTestDataFile(actorJid, problem.getJid(), testDataFile, file.getFilename());
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());

                return showListGradingTestDataFiles(req, form.withGlobalError("Error uploading test data files."), problem, testDataFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

            File testDataFile = file.getFile();
            try {
                programmingProblemService.uploadGradingTestDataFileZipped(actorJid, problem.getJid(), testDataFile);
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(actorJid, problem.getJid());

                return showListGradingTestDataFiles(req, form.withGlobalError("Error uploading test data files."), problem, testDataFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        }

        return redirect(routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listGradingHelperFiles(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        Form<UploadFileForm> uploadFileForm = formFactory.form(UploadFileForm.class);
        List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(actorJid, problem.getJid());

        return showListGradingHelperFiles(req, uploadFileForm, problem, helperFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadGradingHelperFiles(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        Http.MultipartFormData<File> body = req.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file;

        file = body.getFile("file");
        if (file != null) {
            problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

            File helperFile = file.getFile();
            try {
                programmingProblemService.uploadGradingHelperFile(actorJid, problem.getJid(), helperFile, file.getFilename());
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(actorJid, problem.getJid());

                return showListGradingHelperFiles(req, form.withGlobalError("Error uploading helper files."), problem, helperFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

            File helperFile = file.getFile();
            try {
                programmingProblemService.uploadGradingHelperFileZipped(actorJid, problem.getJid(), helperFile);
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(actorJid, problem.getJid());

                return showListGradingHelperFiles(req, form.withGlobalError("Error uploading helper files."), problem, helperFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        }

        return redirect(routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editLanguageRestriction(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        LanguageRestriction languageRestriction;
        try {
            languageRestriction = programmingProblemService.getLanguageRestriction(actorJid, problem.getJid());
        } catch (IOException e) {
            languageRestriction = LanguageRestriction.noRestriction();
        }

        LanguageRestrictionEditForm languageRestrictionEditData = new LanguageRestrictionEditForm();
        languageRestrictionEditData.allowedLanguageNames = LanguageRestrictionAdapter.getFormAllowedLanguageNamesFromLanguageRestriction(languageRestriction);
        languageRestrictionEditData.isAllowedAll = LanguageRestrictionAdapter.getFormIsAllowedAllFromLanguageRestriction(languageRestriction);

        Form<LanguageRestrictionEditForm> languageRestrictionEditForm = formFactory.form(LanguageRestrictionEditForm.class).fill(languageRestrictionEditData);

        return showEditLanguageRestriction(req, languageRestrictionEditForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditLanguageRestriction(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        Form<LanguageRestrictionEditForm> languageRestrictionEditForm = formFactory.form(LanguageRestrictionEditForm.class).bindFromRequest(req);

        if (formHasErrors(languageRestrictionEditForm)) {
            return showEditLanguageRestriction(req, languageRestrictionEditForm, problem);
        }

        problemService.createUserCloneIfNotExists(actorJid, problem.getJid());

        LanguageRestrictionEditForm data = languageRestrictionEditForm.get();
        LanguageRestriction languageRestriction = LanguageRestrictionAdapter.createLanguageRestrictionFromForm(data.allowedLanguageNames, data.isAllowedAll);

        try {
            programmingProblemService.updateLanguageRestriction(actorJid, problem.getJid(), languageRestriction);
        } catch (IOException e) {
            return showEditLanguageRestriction(req, languageRestrictionEditForm.withGlobalError("Error updating language restriction.").withGlobalError("Error updating language restriction"), problem);
        }

        return redirect(routes.ProgrammingProblemGradingController.editLanguageRestriction(problem.getId()));
    }

    private Result showEditGradingEngine(Http.Request req, Form<GradingEngineEditForm> gradingEngineEditForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(editGradingEngineView.render(gradingEngineEditForm, problem));
        template.markBreadcrumbLocation("Update engine", routes.ProgrammingProblemGradingController.editGradingEngine(problem.getId()));
        template.setPageTitle("Problem - Update grading engine");

        return renderTemplate(template, problem);
    }

    private Result showEditGradingConfig(Http.Request req, Form<?> gradingConfForm, Problem problem, String gradingEngine, List<FileInfo> testDataFiles, List<FileInfo> helperFiles) {
        GradingEngineAdapter adapter = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(gradingEngine);
        Call postUpdateGradingConfigCall = routes.ProgrammingProblemGradingController.postEditGradingConfig(problem.getId());
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(adapter.renderUpdateGradingConfig(gradingConfForm, postUpdateGradingConfigCall, testDataFiles, helperFiles));

        if (adapter instanceof ConfigurableWithTokilibFormat) {
            Call updateGradingConfigCall = routes.ProgrammingProblemGradingController.editGradingConfigByTokilibFormat(problem.getId());
            template.transformContent(c -> tokilibLayout.render(updateGradingConfigCall, c));
        } else if (adapter instanceof ConfigurableWithAutoPopulation) {
            Call updateGradingConfigCall = routes.ProgrammingProblemGradingController.editGradingConfigByAutoPopulation(problem.getId());
            template.transformContent(c -> autoPopulationLayout.render(updateGradingConfigCall, c));
        }

        template.markBreadcrumbLocation("Update config", routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
        template.setPageTitle("Problem - Update grading config");

        return renderTemplate(template, problem);
    }

    private Result showListGradingTestDataFiles(Http.Request req, Form<UploadFileForm> uploadFileForm, Problem problem, List<FileInfo> testDataFiles) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listGradingTestDataFilesView.render(uploadFileForm, problem.getId(), testDataFiles));
        template.markBreadcrumbLocation("Test data files", routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        template.setPageTitle("Problem - Grading test data files");

        return renderTemplate(template, problem);
    }

    private Result showListGradingHelperFiles(Http.Request req, Form<UploadFileForm> uploadFileForm, Problem problem, List<FileInfo> helperFiles) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listGradingHelperFilesView.render(uploadFileForm, problem.getId(), helperFiles));
        template.markBreadcrumbLocation("Helper files", routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        template.setPageTitle("Problem - Grading Helper Files");

        return renderTemplate(template, problem);
    }

    private Result showEditLanguageRestriction(Http.Request req, Form<LanguageRestrictionEditForm> languageRestrictionEditForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(editLanguageRestrictionView.render(languageRestrictionEditForm, problem));
        template.markBreadcrumbLocation("Update language restriction", routes.ProgrammingProblemGradingController.editLanguageRestriction(problem.getId()));
        template.setPageTitle("Problem - Update Language Restriction");

        return renderTemplate(template, problem);
    }

    protected Result renderTemplate(HtmlTemplate template, Problem problem) {
        template.addSecondaryTab("Engine", routes.ProgrammingProblemGradingController.editGradingEngine(problem.getId()));
        template.addSecondaryTab("Config", routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
        template.addSecondaryTab("Test data", routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        template.addSecondaryTab("Helpers", routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        template.addSecondaryTab("Language restriction", routes.ProgrammingProblemGradingController.editLanguageRestriction(problem.getId()));

        template.markBreadcrumbLocation("Grading", org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToGrading(problem.getId()));

        return super.renderTemplate(template, problem);
    }
}
