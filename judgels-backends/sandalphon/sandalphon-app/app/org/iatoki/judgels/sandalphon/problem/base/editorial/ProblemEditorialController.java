package org.iatoki.judgels.sandalphon.problem.base.editorial;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import judgels.fs.FileInfo;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemEditorial;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.WorldLanguageRegistry;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.editorial.html.createEditorialView;
import org.iatoki.judgels.sandalphon.problem.base.editorial.html.editEditorialView;
import org.iatoki.judgels.sandalphon.problem.base.editorial.html.listEditorialLanguagesView;
import org.iatoki.judgels.sandalphon.problem.base.editorial.html.listEditorialMediaFilesView;
import org.iatoki.judgels.sandalphon.problem.base.editorial.html.viewEditorialView;
import org.iatoki.judgels.sandalphon.resource.UploadFileForm;
import org.iatoki.judgels.sandalphon.resource.html.katexView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;

public class ProblemEditorialController  extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;

    @Inject
    public ProblemEditorialController(ProblemStore problemStore, ProblemRoleChecker problemRoleChecker) {
        super(problemStore, problemRoleChecker);
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
    }

    @Transactional(readOnly = true)
    public Result viewEditorial(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));

        if (!problemStore.hasEditorial(actorJid, problem.getJid())) {
            return redirect(routes.ProblemEditorialController.createEditorial(problem.getId()));
        }

        String language = getEditorialLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToViewStatement(req, problem, language));

        ProblemEditorial editorial = problemStore.getEditorial(actorJid, problem.getJid(), language);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(viewEditorialView.render(editorial));
        template.addAdditionalScript(katexView.render());

        Set<String> allowedLanguages = problemRoleChecker.getAllowedEditorialLanguagesToView(req, problem);

        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        template.markBreadcrumbLocation("View editorial", routes.ProblemEditorialController.viewEditorial(problemId));
        template.setPageTitle("Problem - View editorial");

        return renderEditorialTemplate(template, problem)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    public Result createEditorial(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToUpdateStatement(req, problem));

        Form<EditorialCreateForm> form = formFactory.form(EditorialCreateForm.class);

        return showCreateEditorial(req, form, problem);
    }

    @Transactional(readOnly = true)
    public Result postCreateEditorial(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToUpdateStatement(req, problem));

        Form<EditorialCreateForm> form = formFactory.form(EditorialCreateForm.class).bindFromRequest(req);

        if (formHasErrors(form)) {
            return showCreateEditorial(req, form, problem);
        }

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        EditorialCreateForm data = form.get();

        problemStore.initEditorials(actorJid, problem.getJid(), data.initLanguageCode);

        return redirect(routes.ProblemEditorialController.editEditorial(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(data.initLanguageCode));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editEditorial(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getEditorialLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToUpdateStatementInLanguage(req, problem, language));

        ProblemEditorial editorial = problemStore.getEditorial(actorJid, problem.getJid(), language);

        EditorialEditForm data = new EditorialEditForm();
        data.text = editorial.getText();

        Form<EditorialEditForm> form = formFactory.form(EditorialEditForm.class).fill(data);

        Set<String> allowedLanguages = problemRoleChecker.getAllowedEditorialLanguagesToUpdate(req, problem);

        return showEditEditorial(req, language, form, problem, allowedLanguages)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditEditorial(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getEditorialLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToUpdateStatementInLanguage(req, problem, language));

        Form<EditorialEditForm> form = formFactory.form(EditorialEditForm.class).bindFromRequest(req);
        if (formHasErrors(form)) {
            Set<String> allowedLanguages = problemRoleChecker.getAllowedEditorialLanguagesToUpdate(req, problem);
            return showEditEditorial(req, language, form, problem, allowedLanguages);
        }

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        EditorialEditForm data = form.get();
        ProblemEditorial editorial = new ProblemEditorial.Builder()
                .text(JudgelsPlayUtils.toSafeHtml(data.text))
                .build();

        problemStore.updateEditorial(actorJid, problem.getJid(), language, editorial);

        return redirect(routes.ProblemEditorialController.viewEditorial(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listEditorialMediaFiles(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));

        Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
        boolean isAllowedToUploadMediaFiles = problemRoleChecker.isAllowedToUploadStatementResources(req, problem);
        List<FileInfo> mediaFiles = problemStore.getEditorialMediaFiles(actorJid, problem.getJid());

        return showListEditorialMediaFiles(req, form, problem, mediaFiles, isAllowedToUploadMediaFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadEditorialMediaFiles(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToUploadStatementResources(req, problem));

        Http.MultipartFormData<Files.TemporaryFile> body = req.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> file;

        file = body.getFile("file");
        if (file != null) {
            File mediaFile = file.getRef().path().toFile();
            problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());
            problemStore.uploadEditorialMediaFile(actorJid, problem.getJid(), mediaFile, file.getFilename());

            return redirect(routes.ProblemEditorialController.listEditorialMediaFiles(problem.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            File mediaFile = file.getRef().path().toFile();
            problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());
            problemStore.uploadEditorialMediaFileZipped(actorJid, problem.getJid(), mediaFile);

            return redirect(routes.ProblemEditorialController.listEditorialMediaFiles(problem.getId()));
        }

        return redirect(routes.ProblemEditorialController.listEditorialMediaFiles(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listEditorialLanguages(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageStatementLanguages(req, problem));

        Map<String, StatementLanguageStatus>
                availableLanguages = problemStore.getEditorialAvailableLanguages(actorJid, problem.getJid());
        String defaultLanguage = problemStore.getEditorialDefaultLanguage(actorJid, problem.getJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listEditorialLanguagesView.render(availableLanguages, defaultLanguage, problem.getId()));
        template.markBreadcrumbLocation("Editorial languages", routes.ProblemEditorialController.listEditorialLanguages(problem.getId()));
        template.setPageTitle("Problem - Editorial languages");

        return renderEditorialTemplate(template, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddEditorialLanguage(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageStatementLanguages(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        String languageCode = formFactory.form().bindFromRequest(req).get("langCode");
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            // TODO should use form so it can be rejected
            throw new IllegalStateException("Languages is not from list.");
        }

        problemStore.addEditorialLanguage(actorJid, problem.getJid(), languageCode);

        return redirect(routes.ProblemEditorialController.listEditorialLanguages(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result enableEditorialLanguage(Http.Request req, long problemId, String languageCode) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageStatementLanguages(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        // TODO should check if language has been enabled
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            return notFound();
        }

        problemStore.enableEditorialLanguage(actorJid, problem.getJid(), languageCode);

        return redirect(routes.ProblemEditorialController.listEditorialLanguages(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result disableEditorialLanguage(Http.Request req, long problemId, String languageCode) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToManageStatementLanguages(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        // TODO should check if language has been enabled
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            return notFound();
        }

        problemStore.disableEditorialLanguage(actorJid, problem.getJid(), languageCode);

        if (getCurrentStatementLanguage(req).equals(languageCode)) {
            language = problemStore.getEditorialDefaultLanguage(actorJid, problem.getJid());
        }

        return redirect(routes.ProblemEditorialController.listEditorialLanguages(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    public Result makeDefaultEditorialLanguage(Http.Request req, long problemId, String languageCode) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageStatementLanguages(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        // TODO should check if language has been enabled
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            return notFound();
        }

        problemStore.makeEditorialDefaultLanguage(actorJid, problem.getJid(), languageCode);

        return redirect(routes.ProblemEditorialController.listEditorialLanguages(problem.getId()));
    }

    private Result showCreateEditorial(Http.Request req, Form<EditorialCreateForm> form, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(createEditorialView.render(form, problem.getId()));
        template.setMainTitle("Create editorial");
        template.markBreadcrumbLocation("Create editorial", routes.ProblemEditorialController.createEditorial(problem.getId()));
        template.markBreadcrumbLocation("Editorials", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToEditorial(problem.getId()));
        template.setPageTitle("Problem - Editorial - Create");
        return renderTemplate(template, problem);
    }

    private Result showEditEditorial(Http.Request req, String language, Form<EditorialEditForm> form, Problem problem, Set<String> allowedLanguages) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(editEditorialView.render(form, problem.getId()));
        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        template.markBreadcrumbLocation("Update editorial", routes.ProblemEditorialController.editEditorial(problem.getId()));
        template.setPageTitle("Problem - Update editorial");

        return renderEditorialTemplate(template, problem);
    }

    private Result showListEditorialMediaFiles(Http.Request req, Form<UploadFileForm> form, Problem problem, List<FileInfo> mediaFiles, boolean isAllowedToUploadMediaFiles) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listEditorialMediaFilesView.render(form, problem.getId(), mediaFiles, isAllowedToUploadMediaFiles));
        template.markBreadcrumbLocation("Media files", routes.ProblemEditorialController.listEditorialMediaFiles(problem.getId()));
        template.setPageTitle("Problem - Editorial - Media files");

        return renderEditorialTemplate(template, problem);
    }
}
