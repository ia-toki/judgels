package org.iatoki.judgels.jerahmeel.training.course;

import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.training.AbstractTrainingCurriculumController;
import org.iatoki.judgels.jerahmeel.training.course.chapter.routes;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.mvc.Result;

public abstract class AbstractTrainingCourseController extends AbstractTrainingCurriculumController {
    protected Result renderTemplate(HtmlTemplate template, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course) {
        template.markBreadcrumbLocation(course.getName(), routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId()));

        return super.renderTemplate(template, curriculum);
    }
}
