package org.iatoki.judgels.jerahmeel.training.course.chapter.submission;

import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.training.course.chapter.AbstractTrainingChapterController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractTrainingSubmissionController extends AbstractTrainingChapterController {
    protected Result renderTemplate(HtmlTemplate template, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter, Chapter chapter) {
        template.addSecondaryTab(Messages.get("training.submissions.programming"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
        template.addSecondaryTab(Messages.get("training.submissions.bundle"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.bundle.routes.TrainingBundleSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));

        return super.renderTemplate(template, curriculum, curriculumCourse, course, courseChapter, chapter);
    }
}
