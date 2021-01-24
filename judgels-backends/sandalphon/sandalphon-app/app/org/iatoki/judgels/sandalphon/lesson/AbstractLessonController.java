package org.iatoki.judgels.sandalphon.lesson;

import java.util.Map;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.AbstractSandalphonController;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.lesson.version.html.versionLocalChangesWarningLayout;
import play.mvc.Http;
import play.mvc.Result;

public class AbstractLessonController extends AbstractSandalphonController {
    private final LessonStore lessonStore;
    private final LessonRoleChecker lessonRoleChecker;

    protected AbstractLessonController(LessonStore lessonStore, LessonRoleChecker lessonRoleChecker) {
        this.lessonStore = lessonStore;
        this.lessonRoleChecker = lessonRoleChecker;
    }

    protected String getStatementLanguage(Http.Request req, Lesson lesson) {
        String userJid = getUserJid(req);
        String currentLanguage = getCurrentStatementLanguage(req);
        Map<String, StatementLanguageStatus> availableLanguages =
                lessonStore.getAvailableLanguages(userJid, lesson.getJid());

        if (currentLanguage == null
                || !availableLanguages.containsKey(currentLanguage)
                || availableLanguages.get(currentLanguage) == StatementLanguageStatus.DISABLED) {
            return lessonStore.getDefaultLanguage(userJid, lesson.getJid());
        }
        return currentLanguage;
    }

    protected Result renderTemplate(HtmlTemplate template, Lesson lesson) {
        appendTabs(template, lesson);
        appendVersionLocalChangesWarning(template, lesson);
        appendTitle(template, lesson);

        template.markBreadcrumbLocation("Lessons", routes.LessonController.index());

        return super.renderTemplate(template);
    }

    protected void appendVersionLocalChangesWarning(HtmlTemplate template, Lesson lesson) {
        String userJid = getUserJid(template.getRequest());
        if (lessonStore.userCloneExists(userJid, lesson.getJid())) {
            template.setWarning(versionLocalChangesWarningLayout.render(lesson.getId(), null));
        }
    }

    private void appendTabs(HtmlTemplate template, Lesson lesson) {
        template.addMainTab("Statements", routes.LessonController.jumpToStatement(lesson.getId()));

        if (lessonRoleChecker.isAuthorOrAbove(template.getRequest(), lesson)) {
            template.addMainTab("Partners", routes.LessonController.jumpToPartners(lesson.getId()));
        }

        template.addMainTab("Versions", routes.LessonController.jumpToVersions(lesson.getId()));
    }

    private void appendTitle(HtmlTemplate template, Lesson lesson) {
        template.setMainTitle("#" + lesson.getId() + ": " + lesson.getSlug());

        if (lessonRoleChecker.isAllowedToUpdateLesson(template.getRequest(), lesson)) {
            template.addMainButton("Update lesson", routes.LessonController.editLesson(lesson.getId()));
        } else {
            template.addMainButton("View lesson", routes.LessonController.viewLesson(lesson.getId()));
        }
    }
}
