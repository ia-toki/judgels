package org.iatoki.judgels.sandalphon.lesson.statement;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
import org.iatoki.judgels.sandalphon.lesson.LessonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.LessonNotFoundException;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import org.iatoki.judgels.sandalphon.lesson.statement.html.editStatementView;
import org.iatoki.judgels.sandalphon.lesson.statement.html.lessonStatementView;
import org.iatoki.judgels.sandalphon.lesson.statement.html.listStatementLanguagesView;
import org.iatoki.judgels.sandalphon.lesson.statement.html.listStatementMediaFilesView;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.statementLanguageSelectionLayout;
import org.iatoki.judgels.sandalphon.resource.html.katexView;
import org.iatoki.judgels.sandalphon.resource.UpdateStatementForm;
import org.iatoki.judgels.sandalphon.resource.UploadFileForm;
import org.iatoki.judgels.sandalphon.resource.WorldLanguageRegistry;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class LessonStatementController extends AbstractLessonController {

    private final LessonService lessonService;

    @Inject
    public LessonStatementController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);
        try {
            LessonControllerUtils.establishStatementLanguage(lessonService, lesson);
        } catch (IOException e) {
            return notFound();
        }

        if (!LessonControllerUtils.isAllowedToViewStatement(lessonService, lesson)) {
            return notFound();
        }

        LessonStatement statement;
        try {
            statement = lessonService.getStatement(IdentityUtils.getUserJid(), lesson.getJid(), LessonControllerUtils.getCurrentStatementLanguage());
        } catch (IOException e) {
            statement = new LessonStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(LessonControllerUtils.getCurrentStatementLanguage()))
                    .text(LessonStatementUtils.getDefaultText(LessonControllerUtils.getCurrentStatementLanguage()))
                    .build();
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(lessonStatementView.render(statement));
        template.addAdditionalScript(katexView.render());

        Set<String> allowedLanguages;
        try {
            allowedLanguages = LessonControllerUtils.getAllowedLanguagesToView(lessonService, lesson);
        } catch (IOException e) {
            return notFound();
        }


        appendStatementLanguageSelection(template, LessonControllerUtils.getCurrentStatementLanguage(), allowedLanguages, org.iatoki.judgels.sandalphon.lesson.routes.LessonController.switchLanguage(lesson.getId()));
        template.markBreadcrumbLocation("View statement", routes.LessonStatementController.viewStatement(lessonId));
        template.setPageTitle("Lesson - View statement");

        return renderTemplate(template, lessonService, lesson);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editStatement(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);
        try {
            LessonControllerUtils.establishStatementLanguage(lessonService, lesson);
        } catch (IOException e) {
            return notFound();
        }

        if (!LessonControllerUtils.isAllowedToUpdateStatementInLanguage(lessonService, lesson)) {
            return notFound();
        }

        LessonStatement statement;
        try {
            statement = lessonService.getStatement(IdentityUtils.getUserJid(), lesson.getJid(), ProblemControllerUtils.getCurrentStatementLanguage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UpdateStatementForm updateStatementData = new UpdateStatementForm();
        updateStatementData.title = statement.getTitle();
        updateStatementData.text = statement.getText();

        Form<UpdateStatementForm> updateStatementForm = formFactory.form(UpdateStatementForm.class).fill(updateStatementData);

        Set<String> allowedLanguages;
        try {
            allowedLanguages = LessonControllerUtils.getAllowedLanguagesToUpdate(lessonService, lesson);
        } catch (IOException e) {
            return notFound();
        }

        return showEditStatement(updateStatementForm, lesson, allowedLanguages);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditStatement(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);
        try {
            LessonControllerUtils.establishStatementLanguage(lessonService, lesson);
        } catch (IOException e) {
            return notFound();
        }

        if (!LessonControllerUtils.isAllowedToUpdateStatementInLanguage(lessonService, lesson)) {
            return notFound();
        }

        Form<UpdateStatementForm> updateStatementForm = formFactory.form(UpdateStatementForm.class).bindFromRequest();
        if (formHasErrors(updateStatementForm)) {
            try {
                Set<String> allowedLanguages = LessonControllerUtils.getAllowedLanguagesToUpdate(lessonService, lesson);
                return showEditStatement(updateStatementForm, lesson, allowedLanguages);
            } catch (IOException e) {
                return notFound();
            }
        }

        lessonService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), lesson.getJid());

        try {
            UpdateStatementForm updateStatementData = updateStatementForm.get();
            lessonService.updateStatement(IdentityUtils.getUserJid(), lesson.getJid(), LessonControllerUtils.getCurrentStatementLanguage(), new LessonStatement.Builder()
                    .title(updateStatementData.title)
                    .text(JudgelsPlayUtils.toSafeHtml(updateStatementData.text))
                    .build());
        } catch (IOException e) {
            try {
                Set<String> allowedLanguages = LessonControllerUtils.getAllowedLanguagesToUpdate(lessonService, lesson);
                return showEditStatement(updateStatementForm.withGlobalError("Error updating statement."), lesson, allowedLanguages);
            } catch (IOException e2) {
                return notFound();
            }
        }

        return redirect(routes.LessonStatementController.editStatement(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listStatementMediaFiles(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        Form<UploadFileForm> uploadFileForm = formFactory.form(UploadFileForm.class);
        boolean isAllowedToUploadMediaFiles = LessonControllerUtils.isAllowedToUploadStatementResources(lessonService, lesson);
        List<FileInfo> mediaFiles = lessonService.getStatementMediaFiles(IdentityUtils.getUserJid(), lesson.getJid());

        return showListStatementMediaFiles(uploadFileForm, lesson, mediaFiles, isAllowedToUploadMediaFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadStatementMediaFiles(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToUploadStatementResources(lessonService, lesson)) {
            return notFound();
        }

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file;

        file = body.getFile("file");
        if (file != null) {
            File mediaFile = file.getFile();
            lessonService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), lesson.getJid());

            try {
                lessonService.uploadStatementMediaFile(IdentityUtils.getUserJid(), lesson.getJid(), mediaFile, file.getFilename());
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                boolean isAllowedToUploadMediaFiles = LessonControllerUtils.isAllowedToUploadStatementResources(lessonService, lesson);
                List<FileInfo> mediaFiles = lessonService.getStatementMediaFiles(IdentityUtils.getUserJid(), lesson.getJid());

                return showListStatementMediaFiles(form.withGlobalError("Error uploading media files."), lesson, mediaFiles, isAllowedToUploadMediaFiles);
            }

            return redirect(routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            File mediaFile = file.getFile();
            lessonService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), lesson.getJid());

            try {
                lessonService.uploadStatementMediaFileZipped(IdentityUtils.getUserJid(), lesson.getJid(), mediaFile);
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                boolean isAllowedToUploadMediaFiles = LessonControllerUtils.isAllowedToUploadStatementResources(lessonService, lesson);
                List<FileInfo> mediaFiles = lessonService.getStatementMediaFiles(IdentityUtils.getUserJid(), lesson.getJid());

                return showListStatementMediaFiles(form.withGlobalError("Error uploading media files."), lesson, mediaFiles, isAllowedToUploadMediaFiles);
            }

            return redirect(routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
        }

        return redirect(routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listStatementLanguages(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToManageStatementLanguages(lessonService, lesson)) {
            return notFound();
        }

        Map<String, StatementLanguageStatus> availableLanguages;
        String defaultLanguage;
        try {
            availableLanguages = lessonService.getAvailableLanguages(IdentityUtils.getUserJid(), lesson.getJid());
            defaultLanguage = lessonService.getDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listStatementLanguagesView.render(availableLanguages, defaultLanguage, lesson.getId()));
        template.markBreadcrumbLocation("Statement languages", routes.LessonStatementController.listStatementLanguages(lesson.getId()));
        template.setPageTitle("Lesson - Statement languages");

        return renderTemplate(template, lessonService, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddStatementLanguage(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToManageStatementLanguages(lessonService, lesson)) {
            return notFound();
        }

        lessonService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), lesson.getJid());

        String languageCode;
        try {
            languageCode = formFactory.form().bindFromRequest().get("langCode");
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                // TODO should use form so it can be rejected
                throw new IllegalStateException("Languages is not from list.");
            }

            lessonService.addLanguage(IdentityUtils.getUserJid(), lesson.getJid(), languageCode);
        } catch (IOException e) {
            // TODO should use form so it can be rejected
            throw new IllegalStateException(e);
        }

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()));
    }

    @Transactional
    public Result enableStatementLanguage(long lessonId, String languageCode) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToManageStatementLanguages(lessonService, lesson)) {
            return notFound();
        }

        lessonService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), lesson.getJid());

        try {
            // TODO should check if language has been enabled
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                return notFound();
            }

            lessonService.enableLanguage(IdentityUtils.getUserJid(), lesson.getJid(), languageCode);
        } catch (IOException e) {
            throw new IllegalStateException("Statement language probably hasn't been added.", e);
        }

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()));
    }

    @Transactional
    public Result disableStatementLanguage(long lessonId, String languageCode) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToManageStatementLanguages(lessonService, lesson)) {
            return notFound();
        }

        lessonService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), lesson.getJid());

        try {
            // TODO should check if language has been enabled
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                return notFound();
            }

            lessonService.disableLanguage(IdentityUtils.getUserJid(), lesson.getJid(), languageCode);

            if (LessonControllerUtils.getCurrentStatementLanguage().equals(languageCode)) {
                LessonControllerUtils.setCurrentStatementLanguage(lessonService.getDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Statement language probably hasn't been added.", e);
        }

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()));
    }

    @Transactional
    public Result makeDefaultStatementLanguage(long lessonId, String languageCode) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToManageStatementLanguages(lessonService, lesson)) {
            return notFound();
        }

        lessonService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), lesson.getJid());

        try {
            // TODO should check if language has been enabled
            if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
                return notFound();
            }

            lessonService.makeDefaultLanguage(IdentityUtils.getUserJid(), lesson.getJid(), languageCode);
        } catch (IOException e) {
            throw new IllegalStateException("Statement language probably hasn't been added.", e);
        }

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()));
    }

    private Result showEditStatement(Form<UpdateStatementForm> updateStatementForm, Lesson lesson, Set<String> allowedLanguages) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editStatementView.render(updateStatementForm, lesson.getId()));
        appendStatementLanguageSelection(template, LessonControllerUtils.getCurrentStatementLanguage(), allowedLanguages, org.iatoki.judgels.sandalphon.lesson.routes.LessonController.switchLanguage(lesson.getId()));
        template.markBreadcrumbLocation("Update statement", routes.LessonStatementController.editStatement(lesson.getId()));

        template.setPageTitle("Lesson - Update statement");

        return renderTemplate(template, lessonService, lesson);
    }

    private Result showListStatementMediaFiles(Form<UploadFileForm> uploadFileForm, Lesson lesson, List<FileInfo> mediaFiles, boolean isAllowedToUploadMediaFiles) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listStatementMediaFilesView.render(uploadFileForm, lesson.getId(), mediaFiles, isAllowedToUploadMediaFiles));
        template.markBreadcrumbLocation("Media files", routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
        template.setPageTitle("Lesson - Statement - Media files");

        return renderTemplate(template, lessonService, lesson);
    }

    protected Result renderTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        template.addSecondaryTab("View", routes.LessonStatementController.viewStatement(lesson.getId()));

        if (LessonControllerUtils.isAllowedToUpdateStatement(lessonService, lesson)) {
            template.addSecondaryTab("Update", routes.LessonStatementController.editStatement(lesson.getId()));
        }

        template.addSecondaryTab("Media", routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));

        if (LessonControllerUtils.isAllowedToManageStatementLanguages(lessonService, lesson)) {
            template.addSecondaryTab("Languages", routes.LessonStatementController.listStatementLanguages(lesson.getId()));
        }

        template.markBreadcrumbLocation("Statements", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToStatement(lesson.getId()));

        return super.renderTemplate(template, lessonService, lesson);
    }

    private void appendStatementLanguageSelection(HtmlTemplate template, String currentLanguage, Set<String> allowedLanguages, Call target) {
        template.transformContent(c -> statementLanguageSelectionLayout.render(target.absoluteURL(Controller.request(), Controller.request().secure()), allowedLanguages, currentLanguage, c));
    }
}
