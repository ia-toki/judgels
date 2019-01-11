package org.iatoki.judgels.jerahmeel.training.course.chapter;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.descriptionHtmlLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionAndBackLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithBackLayout;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import play.i18n.Messages;

public final class TrainingChapterControllerUtils {

    private TrainingChapterControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter, Chapter chapter) {
        ImmutableList.Builder<InternalLink> tabLinksBuilder = ImmutableList.builder();
        tabLinksBuilder.add(new InternalLink(Messages.get("chapter.lessons"), org.iatoki.judgels.jerahmeel.training.course.chapter.lesson.routes.TrainingLessonController.viewLessons(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())));
        tabLinksBuilder.add(new InternalLink(Messages.get("chapter.problems"), org.iatoki.judgels.jerahmeel.training.course.chapter.problem.routes.TrainingProblemController.viewProblems(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())));
        if (JerahmeelUtils.isGuest()) {
            tabLinksBuilder.add(new InternalLink(Messages.get("chapter.submissions"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())));
        } else {
            tabLinksBuilder.add(new InternalLink(Messages.get("chapter.submissions"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.routes.TrainingProgrammingSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())));
        }
        content.appendLayout(c -> tabLayout.render(tabLinksBuilder.build(), c));
        if (!chapter.getDescription().isEmpty()) {
            content.appendLayout(c -> descriptionHtmlLayout.render(chapter.getDescription(), c));
        }
        if (JerahmeelUtils.hasRole("admin")) {
            content.appendLayout(c -> headingWithActionAndBackLayout.render(
                            Messages.get("chapter.chapter") + " #" + chapter.getId() + ": " + chapter.getName(),
                            new InternalLink(Messages.get("commons.update"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.editChapterGeneral(chapter.getId())),
                            new InternalLink(Messages.get("training.backTo") + " " + course.getName(), org.iatoki.judgels.jerahmeel.training.course.chapter.routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId())),
                            c)
            );
        } else {
            content.appendLayout(c -> headingWithBackLayout.render(
                            Messages.get("chapter.chapter") + " " + courseChapter.getAlias() + ": " + chapter.getName(),
                            new InternalLink(Messages.get("training.backTo") + " " + course.getName(), org.iatoki.judgels.jerahmeel.training.course.chapter.routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId())),
                            c)
            );
        }
    }

    public static void appendSubmissionSubtabLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter) {
        content.appendLayout(c -> subtabLayout.render(ImmutableList.of(
                        new InternalLink(Messages.get("training.submissions.programming"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())),
                        new InternalLink(Messages.get("training.submissions.bundle"), org.iatoki.judgels.jerahmeel.training.course.chapter.submission.bundle.routes.TrainingBundleSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()))
                ), c)
        );
    }
}
