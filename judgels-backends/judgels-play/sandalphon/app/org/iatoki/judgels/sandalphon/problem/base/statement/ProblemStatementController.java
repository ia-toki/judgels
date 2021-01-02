package org.iatoki.judgels.sandalphon.problem.base.statement;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.editStatementView;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.listStatementLanguagesView;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.listStatementMediaFilesView;
import org.iatoki.judgels.sandalphon.problem.programming.statement.ProgrammingProblemStatementUtils;
import org.iatoki.judgels.sandalphon.resource.UpdateStatementForm;
import org.iatoki.judgels.sandalphon.resource.UploadFileForm;
import org.iatoki.judgels.sandalphon.resource.WorldLanguageRegistry;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class ProblemStatementController extends AbstractProblemController {

    private final ProblemService problemService;

    @Inject
    public ProblemStatementController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (problem.getType().equals(ProblemType.PROGRAMMING)) {
            return redirect(org.iatoki.judgels.sandalphon.problem.programming.statement.routes.ProgrammingProblemStatementController.viewStatement(problem.getId()));
        } else if (problem.getType().equals(ProblemType.BUNDLE)) {
            return redirect(org.iatoki.judgels.sandalphon.problem.bundle.statement.routes.BundleProblemStatementController.viewStatement(problem.getId()));
        }

        return badRequest();
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editStatement(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);
        try {
            ProblemControllerUtils.establishStatementLanguage(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }

        if (!ProblemControllerUtils.isAllowedToUpdateStatementInLanguage(problemService, problem)) {
            return notFound();
        }

        ProblemStatement statement;
        try {
            statement = problemService.getStatement(IdentityUtils.getUserJid(), problem.getJid(), ProblemControllerUtils.getCurrentStatementLanguage());
        } catch (IOException e) {
            if (ProblemType.PROGRAMMING.equals(problem.getType())) {
                statement = new ProblemStatement.Builder()
                        .title(ProblemStatementUtils.getDefaultTitle(ProblemControllerUtils.getCurrentStatementLanguage()))
                        .text(ProgrammingProblemStatementUtils.getDefaultText(ProblemControllerUtils.getCurrentStatementLanguage()))
                        .build();
            } else {
                throw new IllegalStateException("Problem besides programming has not been defined");
            }
        }

        UpdateStatementForm updateStatementData = new UpdateStatementForm();
        updateStatementData.title = statement.getTitle();
        updateStatementData.text = statement.getText();

        Form<UpdateStatementForm> updateStatementForm = formFactory.form(UpdateStatementForm.class).fill(updateStatementData);

        Set<String> allowedLanguages;
        try {
            allowedLanguages = ProblemControllerUtils.getAllowedLanguagesToUpdate(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }

        return showEditStatement(updateStatementForm, problem, allowedLanguages);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditStatement(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);
        try {
            ProblemControllerUtils.establishStatementLanguage(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }

        if (!ProblemControllerUtils.isAllowedToUpdateStatementInLanguage(problemService, problem)) {
            return notFound();
        }

        Form<UpdateStatementForm> updateStatementForm = formFactory.form(UpdateStatementForm.class).bindFromRequest();
        if (formHasErrors(updateStatementForm)) {
            try {
                Set<String> allowedLanguages = ProblemControllerUtils.getAllowedLanguagesToUpdate(problemService, problem);
                return showEditStatement(updateStatementForm, problem, allowedLanguages);
            } catch (IOException e) {
                return notFound();
            }
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            UpdateStatementForm updateStatementData = updateStatementForm.get();
            ProblemStatement statement = new ProblemStatement.Builder()
                    .title(updateStatementData.title)
                    .text(JudgelsPlayUtils.toSafeHtml(updateStatementData.text))
                    .build();

            problemService.updateStatement(IdentityUtils.getUserJid(), problem.getJid(), ProblemControllerUtils.getCurrentStatementLanguage(), statement);
        } catch (IOException e) {
            try {
                Set<String> allowedLanguages = ProblemControllerUtils.getAllowedLanguagesToUpdate(problemService, problem);
                return showEditStatement(updateStatementForm.withGlobalError("Error updating statement."), problem, allowedLanguages);
            } catch (IOException e2) {
                return notFound();
            }
        }

        return redirect(routes.ProblemStatementController.editStatement(problem.getId()));
    }


    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listStatementMediaFiles(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        Form<UploadFileForm> uploadFileForm = formFactory.form(UploadFileForm.class);
        boolean isAllowedToUploadMediaFiles = ProblemControllerUtils.isAllowedToUploadStatementResources(problemService, problem);
        List<FileInfo> mediaFiles = problemService.getStatementMediaFiles(IdentityUtils.getUserJid(), problem.getJid());

        return showListStatementMediaFiles(uploadFileForm, problem, mediaFiles, isAllowedToUploadMediaFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadStatementMediaFiles(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToUploadStatementResources(problemService, problem)) {
            return notFound();
        }

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file;

        file = body.getFile("file");
        if (file != null) {
            File mediaFile = file.getFile();
            problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

            try {
                problemService.uploadStatementMediaFile(IdentityUtils.getUserJid(), problem.getJid(), mediaFile, file.getFilename());
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                boolean isAllowedToUploadMediaFiles = ProblemControllerUtils.isAllowedToUploadStatementResources(problemService, problem);
                List<FileInfo> mediaFiles = problemService.getStatementMediaFiles(IdentityUtils.getUserJid(), problem.getJid());

                return showListStatementMediaFiles(form.withGlobalError("Error uploading media files."), problem, mediaFiles, isAllowedToUploadMediaFiles);
            }

            return redirect(routes.ProblemStatementController.listStatementMediaFiles(problem.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            File mediaFile = file.getFile();
            problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

            try {
                problemService.uploadStatementMediaFileZipped(IdentityUtils.getUserJid(), problem.getJid(), mediaFile);
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                boolean isAllowedToUploadMediaFiles = ProblemControllerUtils.isAllowedToUploadStatementResources(problemService, problem);
                List<FileInfo> mediaFiles = problemService.getStatementMediaFiles(IdentityUtils.getUserJid(), problem.getJid());

                return showListStatementMediaFiles(form.withGlobalError("Error uploading media files."), problem, mediaFiles, isAllowedToUploadMediaFiles);
            }

            return redirect(routes.ProblemStatementController.listStatementMediaFiles(problem.getId()));
        }

        return redirect(routes.ProblemStatementController.listStatementMediaFiles(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listStatementLanguages(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToManageStatementLanguages(problemService, problem)) {
            return notFound();
        }

        Map<String, StatementLanguageStatus> availableLanguages;
        String defaultLanguage;
        try {
            availableLanguages = problemService.getAvailableLanguages(IdentityUtils.getUserJid(), problem.getJid());
            defaultLanguage = problemService.getDefaultLanguage(IdentityUtils.getUserJid(), problem.getJid());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listStatementLanguagesView.render(availableLanguages, defaultLanguage, problem.getId()));
        template.markBreadcrumbLocation("Statement languages", routes.ProblemStatementController.listStatementLanguages(problem.getId()));
        template.setPageTitle("Problem - Statement languages");

        return renderStatementTemplate(template, problemService, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddStatementLanguage(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToManageStatementLanguages(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        String languageCode;
        try {
            languageCode = formFactory.form().bindFromRequest().get("langCode");
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                // TODO should use form so it can be rejected
                throw new IllegalStateException("Languages is not from list.");
            }

            problemService.addLanguage(IdentityUtils.getUserJid(), problem.getJid(), languageCode);
        } catch (IOException e) {
            // TODO should use form so it can be rejected
            throw new IllegalStateException(e);
        }

        return redirect(routes.ProblemStatementController.listStatementLanguages(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result enableStatementLanguage(long problemId, String languageCode) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToManageStatementLanguages(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            // TODO should check if language has been enabled
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                return notFound();
            }

            problemService.enableLanguage(IdentityUtils.getUserJid(), problem.getJid(), languageCode);
        } catch (IOException e) {
            throw new IllegalStateException("Statement language probably hasn't been added.", e);
        }

        return redirect(routes.ProblemStatementController.listStatementLanguages(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result disableStatementLanguage(long problemId, String languageCode) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToManageStatementLanguages(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            // TODO should check if language has been enabled
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                return notFound();
            }

            problemService.disableLanguage(IdentityUtils.getUserJid(), problem.getJid(), languageCode);

            if (ProblemControllerUtils.getCurrentStatementLanguage().equals(languageCode)) {
                ProblemControllerUtils.setCurrentStatementLanguage(problemService.getDefaultLanguage(IdentityUtils.getUserJid(), problem.getJid()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Statement language probably hasn't been added.", e);
        }

        return redirect(routes.ProblemStatementController.listStatementLanguages(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result makeDefaultStatementLanguage(long problemId, String languageCode) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToManageStatementLanguages(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            // TODO should check if language has been enabled
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                return notFound();
            }

            problemService.makeDefaultLanguage(IdentityUtils.getUserJid(), problem.getJid(), languageCode);
        } catch (IOException e) {
            throw new IllegalStateException("Statement language probably hasn't been added.", e);
        }

        return redirect(routes.ProblemStatementController.listStatementLanguages(problem.getId()));
    }

    private Result showEditStatement(Form<UpdateStatementForm> updateStatementForm, Problem problem, Set<String> allowedLanguages) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editStatementView.render(updateStatementForm, problem.getId()));
        appendStatementLanguageSelection(template, ProblemControllerUtils.getCurrentStatementLanguage(), allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        template.markBreadcrumbLocation("Update statement", routes.ProblemStatementController.editStatement(problem.getId()));
        template.setPageTitle("Problem - Update statement");

        return renderStatementTemplate(template, problemService, problem);
    }

    private Result showListStatementMediaFiles(Form<UploadFileForm> uploadFileForm, Problem problem, List<FileInfo> mediaFiles, boolean isAllowedToUploadMediaFiles) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listStatementMediaFilesView.render(uploadFileForm, problem.getId(), mediaFiles, isAllowedToUploadMediaFiles));
        template.markBreadcrumbLocation("Media files", routes.ProblemStatementController.listStatementMediaFiles(problem.getId()));
        template.setPageTitle("Problem - Statement - Media files");

        return renderStatementTemplate(template, problemService, problem);
    }
}
