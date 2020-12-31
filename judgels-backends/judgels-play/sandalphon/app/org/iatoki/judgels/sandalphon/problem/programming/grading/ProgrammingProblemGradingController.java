package org.iatoki.judgels.sandalphon.problem.programming.grading;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.engines.GradingEngineRegistry;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.AbstractProgrammingProblemController;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
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
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemGradingController extends AbstractProgrammingProblemController {

    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ProgrammingProblemGradingController(ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editGradingEngine(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        GradingEngineEditForm gradingEngineEditData = new GradingEngineEditForm();
        try  {
            gradingEngineEditData.gradingEngineName = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            gradingEngineEditData.gradingEngineName = GradingEngineRegistry.getInstance().getDefault();
        }

        Form<GradingEngineEditForm> gradingEngineEditForm = Form.form(GradingEngineEditForm.class).fill(gradingEngineEditData);

        return showEditGradingEngine(gradingEngineEditForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditGradingEngine(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        Form<GradingEngineEditForm> gradingEngineEditForm = Form.form(GradingEngineEditForm.class).bindFromRequest(request());

        if (formHasErrors(gradingEngineEditForm)) {
            return showEditGradingEngine(gradingEngineEditForm, problem);
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        String gradingEngine = gradingEngineEditForm.get().gradingEngineName;
        String originalGradingEngine;
        try {
            originalGradingEngine = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            originalGradingEngine = GradingEngineRegistry.getInstance().getDefault();
        }

        try {
            if (!gradingEngine.equals(originalGradingEngine)) {
                GradingConfig config = GradingEngineRegistry.getInstance().get(gradingEngine).createDefaultConfig();
                programmingProblemService.updateGradingConfig(IdentityUtils.getUserJid(), problem.getJid(), config);
            }

            programmingProblemService.updateGradingEngine(IdentityUtils.getUserJid(), problem.getJid(), gradingEngine);
        } catch (IOException e) {
            gradingEngineEditForm.reject("problem.programming.grading.engine.error.cantUpdate");
            return showEditGradingEngine(gradingEngineEditForm, problem);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editGradingConfig(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        GradingConfig config;
        try {
            config = programmingProblemService.getGradingConfig(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
        }
        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());
        List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(IdentityUtils.getUserJid(), problem.getJid());

        Form<?> gradingEngineConfForm = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).createFormFromConfig(config);

        return showEditGradingConfig(gradingEngineConfForm, problem, engine, testDataFiles, helperFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditGradingConfig(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);
        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        Form<?> gradingEngineConfForm = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).createEmptyForm().bindFromRequest(request());

        if (formHasErrors(gradingEngineConfForm)) {
            List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());
            List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(IdentityUtils.getUserJid(), problem.getJid());

            return showEditGradingConfig(gradingEngineConfForm, problem, engine, testDataFiles, helperFiles);
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            GradingConfig config = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).createConfigFromForm(gradingEngineConfForm);
            programmingProblemService.updateGradingConfig(IdentityUtils.getUserJid(), problem.getJid(), config);
        } catch (IOException e) {
            gradingEngineConfForm.reject("problem.programming.grading.config.error.cantUpdate");
            List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());
            List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(IdentityUtils.getUserJid(), problem.getJid());

            return showEditGradingConfig(gradingEngineConfForm, problem, engine, testDataFiles, helperFiles);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional
    public Result editGradingConfigByTokilibFormat(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }

        GradingEngineAdapter adapter = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine);

        if (!(adapter instanceof ConfigurableWithTokilibFormat)) {
            return forbidden();
        }

        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());
        GradingConfig config;
        try {
            config = programmingProblemService.getGradingConfig(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
        }

        try {
            GradingConfig newConfig = ((ConfigurableWithTokilibFormat) adapter).updateConfigWithTokilibFormat(config, testDataFiles);
            programmingProblemService.updateGradingConfig(IdentityUtils.getUserJid(), problem.getJid(), newConfig);
        } catch (IOException e) {
            throw new IllegalStateException("Can't update grading config using tokilib format", e);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional
    public Result editGradingConfigByAutoPopulation(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }
        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());
        GradingEngineAdapter adapter = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine);

        if (!(adapter instanceof ConfigurableWithAutoPopulation)) {
            return forbidden();
        }

        GradingConfig config;
        try {
            config = programmingProblemService.getGradingConfig(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
        }
        GradingConfig newConfig = ((ConfigurableWithAutoPopulation) adapter).updateConfigWithAutoPopulation(config, testDataFiles);

        try {
            programmingProblemService.updateGradingConfig(IdentityUtils.getUserJid(), problem.getJid(), newConfig);
        } catch (IOException e) {
            throw new IllegalStateException("Can't update grading config using auto population", e);
        }

        return redirect(routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listGradingTestDataFiles(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        Form<UploadFileForm> uploadFileForm = Form.form(UploadFileForm.class);
        List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());

        return showListGradingTestDataFiles(uploadFileForm, problem, testDataFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadGradingTestDataFiles(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file;

        file = body.getFile("file");
        if (file != null) {
            problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

            File testDataFile = file.getFile();
            try {
                programmingProblemService.uploadGradingTestDataFile(IdentityUtils.getUserJid(), problem.getJid(), testDataFile, file.getFilename());
            } catch (IOException e) {
                Form<UploadFileForm> form = Form.form(UploadFileForm.class);
                List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());
                form.reject("problem.programming.grading.error.cantUploadTestData");

                return showListGradingTestDataFiles(form, problem, testDataFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

            File testDataFile = file.getFile();
            try {
                programmingProblemService.uploadGradingTestDataFileZipped(IdentityUtils.getUserJid(), problem.getJid(), testDataFile);
            } catch (IOException e) {
                Form<UploadFileForm> form = Form.form(UploadFileForm.class);
                List<FileInfo> testDataFiles = programmingProblemService.getGradingTestDataFiles(IdentityUtils.getUserJid(), problem.getJid());
                form.reject("problem.programming.grading.error.cantUploadTestDataZipped");

                return showListGradingTestDataFiles(form, problem, testDataFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        }

        return redirect(routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listGradingHelperFiles(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        Form<UploadFileForm> uploadFileForm = Form.form(UploadFileForm.class);
        List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(IdentityUtils.getUserJid(), problem.getJid());

        return showListGradingHelperFiles(uploadFileForm, problem, helperFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadGradingHelperFiles(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file;

        file = body.getFile("file");
        if (file != null) {
            problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

            File helperFile = file.getFile();
            try {
                programmingProblemService.uploadGradingHelperFile(IdentityUtils.getUserJid(), problem.getJid(), helperFile, file.getFilename());
            } catch (IOException e) {
                Form<UploadFileForm> form = Form.form(UploadFileForm.class);
                List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(IdentityUtils.getUserJid(), problem.getJid());
                form.reject("problem.programming.grading.error.cantUploadHelper");

                return showListGradingHelperFiles(form, problem, helperFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

            File helperFile = file.getFile();
            try {
                programmingProblemService.uploadGradingHelperFileZipped(IdentityUtils.getUserJid(), problem.getJid(), helperFile);
            } catch (IOException e) {
                Form<UploadFileForm> form = Form.form(UploadFileForm.class);
                List<FileInfo> helperFiles = programmingProblemService.getGradingHelperFiles(IdentityUtils.getUserJid(), problem.getJid());
                form.reject("problem.programming.grading.error.cantUploadHelperZipped");

                return showListGradingHelperFiles(form, problem, helperFiles);
            }

            return redirect(routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        }

        return redirect(routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editLanguageRestriction(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        LanguageRestriction languageRestriction;
        try {
            languageRestriction = programmingProblemService.getLanguageRestriction(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            languageRestriction = LanguageRestriction.noRestriction();
        }

        LanguageRestrictionEditForm languageRestrictionEditData = new LanguageRestrictionEditForm();
        languageRestrictionEditData.allowedLanguageNames = LanguageRestrictionAdapter.getFormAllowedLanguageNamesFromLanguageRestriction(languageRestriction);
        languageRestrictionEditData.isAllowedAll = LanguageRestrictionAdapter.getFormIsAllowedAllFromLanguageRestriction(languageRestriction);

        Form<LanguageRestrictionEditForm> languageRestrictionEditForm = Form.form(LanguageRestrictionEditForm.class).fill(languageRestrictionEditData);

        return showEditLanguageRestriction(languageRestrictionEditForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditLanguageRestriction(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return notFound();
        }

        Form<LanguageRestrictionEditForm> languageRestrictionEditForm = Form.form(LanguageRestrictionEditForm.class).bindFromRequest(request());

        if (formHasErrors(languageRestrictionEditForm)) {
            return showEditLanguageRestriction(languageRestrictionEditForm, problem);
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        LanguageRestrictionEditForm data = languageRestrictionEditForm.get();
        LanguageRestriction languageRestriction = LanguageRestrictionAdapter.createLanguageRestrictionFromForm(data.allowedLanguageNames, data.isAllowedAll);

        try {
            programmingProblemService.updateLanguageRestriction(IdentityUtils.getUserJid(), problem.getJid(), languageRestriction);
        } catch (IOException e) {
            languageRestrictionEditForm.reject("problem.programming.language.error.cantUpdate");
            return showEditLanguageRestriction(languageRestrictionEditForm, problem);
        }

        return redirect(routes.ProgrammingProblemGradingController.editLanguageRestriction(problem.getId()));
    }

    private Result showEditGradingEngine(Form<GradingEngineEditForm> gradingEngineEditForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editGradingEngineView.render(gradingEngineEditForm, problem));
        template.markBreadcrumbLocation(Messages.get("problem.programming.grading.engine.update"), routes.ProgrammingProblemGradingController.editGradingEngine(problem.getId()));
        template.setPageTitle("Problem - Update Grading Engine");

        return renderTemplate(template, problemService, problem);
    }

    private Result showEditGradingConfig(Form<?> gradingConfForm, Problem problem, String gradingEngine, List<FileInfo> testDataFiles, List<FileInfo> helperFiles) {
        GradingEngineAdapter adapter = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(gradingEngine);
        Call postUpdateGradingConfigCall = routes.ProgrammingProblemGradingController.postEditGradingConfig(problem.getId());
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(adapter.renderUpdateGradingConfig(gradingConfForm, postUpdateGradingConfigCall, testDataFiles, helperFiles));

        if (adapter instanceof ConfigurableWithTokilibFormat) {
            Call updateGradingConfigCall = routes.ProgrammingProblemGradingController.editGradingConfigByTokilibFormat(problem.getId());
            template.transformContent(c -> tokilibLayout.render(updateGradingConfigCall, c));
        } else if (adapter instanceof ConfigurableWithAutoPopulation) {
            Call updateGradingConfigCall = routes.ProgrammingProblemGradingController.editGradingConfigByAutoPopulation(problem.getId());
            template.transformContent(c -> autoPopulationLayout.render(updateGradingConfigCall, c));
        }

        template.markBreadcrumbLocation(Messages.get("problem.programming.grading.config.update"), routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
        template.setPageTitle("Problem - Update Grading Config");

        return renderTemplate(template, problemService, problem);
    }

    private Result showListGradingTestDataFiles(Form<UploadFileForm> uploadFileForm, Problem problem, List<FileInfo> testDataFiles) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listGradingTestDataFilesView.render(uploadFileForm, problem.getId(), testDataFiles));
        template.markBreadcrumbLocation(Messages.get("problem.programming.grading.testData.list"), routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        template.setPageTitle("Problem - List Grading Test Data Files");

        return renderTemplate(template, problemService, problem);
    }

    private Result showListGradingHelperFiles(Form<UploadFileForm> uploadFileForm, Problem problem, List<FileInfo> helperFiles) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listGradingHelperFilesView.render(uploadFileForm, problem.getId(), helperFiles));
        template.markBreadcrumbLocation(Messages.get("problem.programming.grading.helper.list"), routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        template.setPageTitle("Problem - List Grading Helper Files");

        return renderTemplate(template, problemService, problem);
    }

    private Result showEditLanguageRestriction(Form<LanguageRestrictionEditForm> languageRestrictionEditForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editLanguageRestrictionView.render(languageRestrictionEditForm, problem));
        template.markBreadcrumbLocation(Messages.get("problem.programming.grading.languageRestriction.update"), routes.ProgrammingProblemGradingController.editLanguageRestriction(problem.getId()));
        template.setPageTitle("Problem - Update Language Restriction");

        return renderTemplate(template, problemService, problem);
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.addSecondaryTab(Messages.get("problem.programming.grading.engine"), routes.ProgrammingProblemGradingController.editGradingEngine(problem.getId()));
        template.addSecondaryTab(Messages.get("problem.programming.grading.config"), routes.ProgrammingProblemGradingController.editGradingConfig(problem.getId()));
        template.addSecondaryTab(Messages.get("problem.programming.grading.testData"), routes.ProgrammingProblemGradingController.listGradingTestDataFiles(problem.getId()));
        template.addSecondaryTab(Messages.get("problem.programming.grading.helper"), routes.ProgrammingProblemGradingController.listGradingHelperFiles(problem.getId()));
        template.addSecondaryTab(Messages.get("problem.programming.grading.languageRestriction"), routes.ProgrammingProblemGradingController.editLanguageRestriction(problem.getId()));

        template.markBreadcrumbLocation(Messages.get("problem.programming.grading"), org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToGrading(problem.getId()));

        return super.renderTemplate(template, problemService, problem);
    }
}
