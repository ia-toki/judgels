package org.iatoki.judgels.jerahmeel.training.course.chapter;

import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.training.course.AbstractTrainingCourseController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractTrainingChapterController extends AbstractTrainingCourseController {
    protected Result renderTemplate(HtmlTemplate template, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter, Chapter chapter) {
        template.markBreadcrumbLocation(course.getName(), routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId()));

        template.addMainTab(Messages.get("chapter.lessons"), org.iatoki.judgels.jerahmeel.training.course.chapter.lesson.routes.TrainingLessonController.viewLessons(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
        template.addMainTab(Messages.get("chapter.problems"), org.iatoki.judgels.jerahmeel.training.course.chapter.problem.routes.TrainingProblemController.viewProblems(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
        if (JerahmeelUtils.isGuest()) {
            template.addMainTab(Messages.get("chapter.submissions"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
        } else {
            template.addMainTab(Messages.get("chapter.submissions"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.routes.TrainingProgrammingSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
        }
        if (!chapter.getDescription().isEmpty()) {
            template.setDescription(chapter.getDescription());
        }
        if (JerahmeelUtils.hasRole("admin")) {
            template.setMainTitle(Messages.get("chapter.chapter") + " #" + chapter.getId() + ": " + chapter.getName());
            template.addMainButton(Messages.get("commons.update"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.editChapterGeneral(chapter.getId()));
        } else {
            template.setMainTitle(Messages.get("chapter.chapter") + " " + courseChapter.getAlias() + ": " + chapter.getName());
        }
        template.setMainBackButton(Messages.get("training.backTo") + " " + course.getName(), org.iatoki.judgels.jerahmeel.training.course.chapter.routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId()));

        return super.renderTemplate(template, curriculum);
    }
}
