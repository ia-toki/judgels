package org.iatoki.judgels.sandalphon.lesson.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.fs.FileInfo;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.lesson.LessonStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.WorldLanguageRegistry;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
import org.iatoki.judgels.sandalphon.lesson.LessonRoleChecker;
import org.iatoki.judgels.sandalphon.lesson.statement.html.editStatementView;
import org.iatoki.judgels.sandalphon.lesson.statement.html.lessonStatementView;
import org.iatoki.judgels.sandalphon.lesson.statement.html.listStatementLanguagesView;
import org.iatoki.judgels.sandalphon.lesson.statement.html.listStatementMediaFilesView;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.statementLanguageSelectionLayout;
import org.iatoki.judgels.sandalphon.resource.UpdateStatementForm;
import org.iatoki.judgels.sandalphon.resource.UploadFileForm;
import org.iatoki.judgels.sandalphon.resource.html.katexView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Files.TemporaryFile;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class LessonStatementController extends AbstractLessonController {
    private final LessonStore lessonStore;
    private final LessonRoleChecker lessonRoleChecker;

    @Inject
    public LessonStatementController(LessonStore lessonStore, LessonRoleChecker lessonRoleChecker) {
        super(lessonStore, lessonRoleChecker);
        this.lessonStore = lessonStore;
        this.lessonRoleChecker = lessonRoleChecker;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        String language = getStatementLanguage(req, lesson);
        checkAllowed(lessonRoleChecker.isAllowedToViewStatement(req, lesson, language));

        LessonStatement statement = lessonStore.getStatement(actorJid, lesson.getJid(), language);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(lessonStatementView.render(statement));
        template.addAdditionalScript(katexView.render());

        Set<String> allowedLanguages = lessonRoleChecker.getAllowedLanguagesToView(req, lesson);

        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.lesson.routes.LessonController.switchLanguage(lesson.getId()));
        template.markBreadcrumbLocation("View statement", routes.LessonStatementController.viewStatement(lessonId));
        template.setPageTitle("Lesson - View statement");

        return renderTemplate(template, lesson)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editStatement(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        String language = getStatementLanguage(req, lesson);
        checkAllowed(lessonRoleChecker.isAllowedToUpdateStatementInLanguage(req, lesson, language));

        LessonStatement statement = lessonStore.getStatement(actorJid, lesson.getJid(), language);

        UpdateStatementForm updateStatementData = new UpdateStatementForm();
        updateStatementData.title = statement.getTitle();
        updateStatementData.text = statement.getText();

        Form<UpdateStatementForm> updateStatementForm = formFactory.form(UpdateStatementForm.class).fill(updateStatementData);

        Set<String> allowedLanguages = lessonRoleChecker.getAllowedLanguagesToUpdate(req, lesson);

        return showEditStatement(req, language, updateStatementForm, lesson, allowedLanguages)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditStatement(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        String language = getStatementLanguage(req, lesson);
        checkAllowed(lessonRoleChecker.isAllowedToUpdateStatementInLanguage(req, lesson, language));

        Form<UpdateStatementForm> updateStatementForm = formFactory.form(UpdateStatementForm.class).bindFromRequest(req);
        if (formHasErrors(updateStatementForm)) {
            Set<String> allowedLanguages = lessonRoleChecker.getAllowedLanguagesToUpdate(req, lesson);
            return showEditStatement(req, language, updateStatementForm, lesson, allowedLanguages);
        }

        lessonStore.createUserCloneIfNotExists(actorJid, lesson.getJid());

        UpdateStatementForm updateStatementData = updateStatementForm.get();
        lessonStore.updateStatement(actorJid, lesson.getJid(), language, new LessonStatement.Builder()
                    .title(updateStatementData.title)
                    .text(JudgelsPlayUtils.toSafeHtml(updateStatementData.text))
                    .build());

        return redirect(routes.LessonStatementController.editStatement(lesson.getId()))
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listStatementMediaFiles(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));

        Form<UploadFileForm> uploadFileForm = formFactory.form(UploadFileForm.class);
        boolean isAllowedToUploadMediaFiles = lessonRoleChecker.isAllowedToUploadStatementResources(req, lesson);
        List<FileInfo> mediaFiles = lessonStore.getStatementMediaFiles(actorJid, lesson.getJid());

        return showListStatementMediaFiles(req, uploadFileForm, lesson, mediaFiles, isAllowedToUploadMediaFiles);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUploadStatementMediaFiles(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToUploadStatementResources(req, lesson));

        Http.MultipartFormData<TemporaryFile> body = req.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> file;

        file = body.getFile("file");
        if (file != null) {
            File mediaFile = file.getRef().path().toFile();
            lessonStore.createUserCloneIfNotExists(actorJid, lesson.getJid());

            try {
                lessonStore.uploadStatementMediaFile(actorJid, lesson.getJid(), mediaFile, file.getFilename());
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                boolean isAllowedToUploadMediaFiles = lessonRoleChecker.isAllowedToUploadStatementResources(req, lesson);
                List<FileInfo> mediaFiles = lessonStore.getStatementMediaFiles(actorJid, lesson.getJid());

                return showListStatementMediaFiles(req, form.withGlobalError("Error uploading media files."), lesson, mediaFiles, isAllowedToUploadMediaFiles);
            }

            return redirect(routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
        }

        file = body.getFile("fileZipped");
        if (file != null) {
            File mediaFile = file.getRef().path().toFile();
            lessonStore.createUserCloneIfNotExists(actorJid, lesson.getJid());

            try {
                lessonStore.uploadStatementMediaFileZipped(actorJid, lesson.getJid(), mediaFile);
            } catch (IOException e) {
                Form<UploadFileForm> form = formFactory.form(UploadFileForm.class);
                boolean isAllowedToUploadMediaFiles = lessonRoleChecker.isAllowedToUploadStatementResources(req, lesson);
                List<FileInfo> mediaFiles = lessonStore.getStatementMediaFiles(actorJid, lesson.getJid());

                return showListStatementMediaFiles(req, form.withGlobalError("Error uploading media files."), lesson, mediaFiles, isAllowedToUploadMediaFiles);
            }

            return redirect(routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
        }

        return redirect(routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listStatementLanguages(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToManageStatementLanguages(req, lesson));

        Map<String, StatementLanguageStatus> availableLanguages = lessonStore.getAvailableLanguages(actorJid, lesson.getJid());
        String defaultLanguage = lessonStore.getDefaultLanguage(actorJid, lesson.getJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listStatementLanguagesView.render(availableLanguages, defaultLanguage, lesson.getId()));
        template.markBreadcrumbLocation("Statement languages", routes.LessonStatementController.listStatementLanguages(lesson.getId()));
        template.setPageTitle("Lesson - Statement languages");

        return renderTemplate(template, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddStatementLanguage(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToManageStatementLanguages(req, lesson));

        lessonStore.createUserCloneIfNotExists(actorJid, lesson.getJid());

        String languageCode = formFactory.form().bindFromRequest(req).get("langCode");
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            // TODO should use form so it can be rejected
            throw new IllegalStateException("Languages is not from list.");
        }

        lessonStore.addLanguage(actorJid, lesson.getJid(), languageCode);

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()));
    }

    @Transactional
    public Result enableStatementLanguage(Http.Request req, long lessonId, String languageCode) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToManageStatementLanguages(req, lesson));

        lessonStore.createUserCloneIfNotExists(actorJid, lesson.getJid());

        // TODO should check if language has been enabled
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            return notFound();
        }

        lessonStore.enableLanguage(actorJid, lesson.getJid(), languageCode);

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()));
    }

    @Transactional
    public Result disableStatementLanguage(Http.Request req, long lessonId, String languageCode) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        String language = getStatementLanguage(req, lesson);
        checkAllowed(lessonRoleChecker.isAllowedToManageStatementLanguages(req, lesson));

        lessonStore.createUserCloneIfNotExists(actorJid, lesson.getJid());

        // TODO should check if language has been enabled
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            return notFound();
        }

        lessonStore.disableLanguage(actorJid, lesson.getJid(), languageCode);

        if (language.equals(languageCode)) {
            language = lessonStore.getDefaultLanguage(actorJid, lesson.getJid());
        }

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()))
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional
    public Result makeDefaultStatementLanguage(Http.Request req, long lessonId, String languageCode) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToManageStatementLanguages(req, lesson));

        lessonStore.createUserCloneIfNotExists(actorJid, lesson.getJid());

        // TODO should check if language has been enabled
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(languageCode)) {
            return notFound();
        }

        lessonStore.makeDefaultLanguage(actorJid, lesson.getJid(), languageCode);

        return redirect(routes.LessonStatementController.listStatementLanguages(lesson.getId()));
    }

    private Result showEditStatement(Http.Request req, String language, Form<UpdateStatementForm> updateStatementForm, Lesson lesson, Set<String> allowedLanguages) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(editStatementView.render(updateStatementForm, lesson.getId()));
        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.lesson.routes.LessonController.switchLanguage(lesson.getId()));
        template.markBreadcrumbLocation("Update statement", routes.LessonStatementController.editStatement(lesson.getId()));

        template.setPageTitle("Lesson - Update statement");

        return renderTemplate(template, lesson);
    }

    private Result showListStatementMediaFiles(Http.Request req, Form<UploadFileForm> uploadFileForm, Lesson lesson, List<FileInfo> mediaFiles, boolean isAllowedToUploadMediaFiles) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listStatementMediaFilesView.render(uploadFileForm, lesson.getId(), mediaFiles, isAllowedToUploadMediaFiles));
        template.markBreadcrumbLocation("Media files", routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));
        template.setPageTitle("Lesson - Statement - Media files");

        return renderTemplate(template, lesson);
    }

    protected Result renderTemplate(HtmlTemplate template, Lesson lesson) {
        template.addSecondaryTab("View", routes.LessonStatementController.viewStatement(lesson.getId()));

        if (lessonRoleChecker.isAllowedToUpdateStatement(template.getRequest(), lesson)) {
            template.addSecondaryTab("Update", routes.LessonStatementController.editStatement(lesson.getId()));
        }

        template.addSecondaryTab("Media", routes.LessonStatementController.listStatementMediaFiles(lesson.getId()));

        if (lessonRoleChecker.isAllowedToManageStatementLanguages(template.getRequest(), lesson)) {
            template.addSecondaryTab("Languages", routes.LessonStatementController.listStatementLanguages(lesson.getId()));
        }

        template.markBreadcrumbLocation("Statements", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToStatement(lesson.getId()));

        return super.renderTemplate(template, lesson);
    }

    private void appendStatementLanguageSelection(HtmlTemplate template, String currentLanguage, Set<String> allowedLanguages, Call target) {
        Http.Request req = template.getRequest();
        template.transformContent(c -> statementLanguageSelectionLayout.render(target.url(), ImmutableList.copyOf(allowedLanguages), currentLanguage, c));
    }
}
