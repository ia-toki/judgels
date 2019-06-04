package org.iatoki.judgels.jerahmeel.course;

import org.iatoki.judgels.jerahmeel.AbstractJerahmeelController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractCourseController extends AbstractJerahmeelController {
    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation(Messages.get("course.courses"), routes.CourseController.viewCourses());

        return super.renderTemplate(template);
    }

    protected void appendTabs(HtmlTemplate template, Course course) {
        template.addMainTab(Messages.get("course.update"), routes.CourseController.editCourseGeneral(course.getId()));
        template.addMainTab(Messages.get("course.chapters"), routes.CourseController.jumpToChapters(course.getId()));

        template.setMainTitle(Messages.get("course.course") + " #" + course.getId() + ": " + course.getName());
    }
}
