package org.iatoki.judgels.jerahmeel.training.course;

import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.descriptionHtmlLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionAndBackLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithBackLayout;
import play.i18n.Messages;

public final class TrainingCourseControllerUtils {

    private TrainingCourseControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course) {
        if (!course.getDescription().isEmpty()) {
            content.appendLayout(c -> descriptionHtmlLayout.render(course.getDescription(), c));
        }
        if (JerahmeelUtils.hasRole("admin")) {
            content.appendLayout(c -> headingWithActionAndBackLayout.render(
                    Messages.get("course.course") + " #" + course.getId() + ": " + course.getName(),
                    new InternalLink(Messages.get("commons.update"), org.iatoki.judgels.jerahmeel.course.routes.CourseController.editCourseGeneral(course.getId())),
                    new InternalLink(Messages.get("training.backTo") + " " + curriculum.getName(), routes.TrainingCourseController.viewCourses(curriculum.getId())),
                    c)
            );
        } else {
            content.appendLayout(c -> headingWithBackLayout.render(Messages.get("course.course") + " " + curriculumCourse.getAlias() + ": " + course.getName(),
                    new InternalLink(Messages.get("training.backTo") + " " + curriculum.getName(), routes.TrainingCourseController.viewCourses(curriculum.getId())),
                    c)
            );
        }
    }
}
