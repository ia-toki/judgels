package org.iatoki.judgels.sandalphon.lesson;

import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.AbstractSandalphonController;
import org.iatoki.judgels.sandalphon.lesson.version.html.versionLocalChangesWarningLayout;
import play.mvc.Result;

public class AbstractLessonController extends AbstractSandalphonController {
    protected Result renderTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        appendTabs(template, lesson);
        appendVersionLocalChangesWarning(template, lessonService, lesson);
        appendTitle(template, lessonService, lesson);

        template.markBreadcrumbLocation("Lessons", routes.LessonController.index());
        
        return super.renderTemplate(template);
    }

    protected void appendVersionLocalChangesWarning(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        if (lessonService.userCloneExists(IdentityUtils.getUserJid(), lesson.getJid())) {
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
