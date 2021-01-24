package org.iatoki.judgels.sandalphon.lesson;

import java.util.Map;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.AbstractSandalphonController;
import org.iatoki.judgels.sandalphon.SandalphonSessionUtils;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.lesson.version.html.versionLocalChangesWarningLayout;
import play.mvc.Http;
import play.mvc.Result;

public class AbstractLessonController extends AbstractSandalphonController {
    private final LessonService lessonService;

    protected AbstractLessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    protected String getStatementLanguage(Http.Request req, Lesson lesson) {
        String userJid = getUserJid(req);
        String currentLanguage = SandalphonSessionUtils.getCurrentStatementLanguage(req);
        Map<String, StatementLanguageStatus>
                availableLanguages = lessonService.getAvailableLanguages(userJid, lesson.getJid());

        if (currentLanguage == null
                || !availableLanguages.containsKey(currentLanguage)
                || availableLanguages.get(currentLanguage) == StatementLanguageStatus.DISABLED) {
            return lessonService.getDefaultLanguage(userJid, lesson.getJid());
        }
        return currentLanguage;
    }

    protected Result renderTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        appendTabs(template, lesson);
        appendVersionLocalChangesWarning(template, lessonService, lesson);
        appendTitle(template, lessonService, lesson);

        template.markBreadcrumbLocation("Lessons", routes.LessonController.index());

        return super.renderTemplate(template);
    }

    protected void appendVersionLocalChangesWarning(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        String userJid = getUserJid(template.getRequest());
        if (lessonService.userCloneExists(userJid, lesson.getJid())) {
            template.setWarning(versionLocalChangesWarningLayout.render(lesson.getId(), null));
        }
    }

    private void appendTabs(HtmlTemplate template, Lesson lesson) {
        template.addMainTab("Statements", routes.LessonController.jumpToStatement(lesson.getId()));

        if (LessonControllerUtils.isAuthorOrAbove(lesson)) {
            template.addMainTab("Partners", routes.LessonController.jumpToPartners(lesson.getId()));
        }

        template.addMainTab("Versions", routes.LessonController.jumpToVersions(lesson.getId()));
    }

    private void appendTitle(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        template.setMainTitle("#" + lesson.getId() + ": " + lesson.getSlug());

        if (LessonControllerUtils.isAllowedToUpdateLesson(lessonService, lesson)) {
            template.addMainButton("Update lesson", routes.LessonController.editLesson(lesson.getId()));
        } else {
            template.addMainButton("View lesson", routes.LessonController.viewLesson(lesson.getId()));
        }
    }
}
