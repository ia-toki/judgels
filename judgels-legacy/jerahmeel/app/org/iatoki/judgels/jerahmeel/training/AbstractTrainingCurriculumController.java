package org.iatoki.judgels.jerahmeel.training;

import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.training.course.routes;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.mvc.Result;

public abstract class AbstractTrainingCurriculumController extends AbstractTrainingController {
    protected Result renderTemplate(HtmlTemplate template, Curriculum curriculum) {
        template.markBreadcrumbLocation(curriculum.getName(), routes.TrainingCourseController.viewCourses(curriculum.getId()));

        return super.renderTemplate(template);
    }
}
