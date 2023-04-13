package judgels.jerahmeel.api.course.chapter;

import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_2_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.sandalphon.api.problem.ProblemType;
import org.junit.jupiter.api.Test;

class CourseChapterServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Course courseA = courseService.createCourse(ADMIN_HEADER, new CourseCreateData.Builder()
                .slug("course-a")
                .name("Course A")
                .build());
        Course courseB = courseService.createCourse(ADMIN_HEADER, new CourseCreateData.Builder()
                .slug("course-b")
                .name("Course B")
                .build());

        Chapter chapterA = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());
        chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter C")
                .build());

        courseChapterService.setChapters(ADMIN_HEADER, courseA.getJid(), ImmutableList.of(
                new CourseChapter.Builder().alias("A").chapterJid(chapterA.getJid()).build(),
                new CourseChapter.Builder().alias("B").chapterJid(chapterB.getJid()).build()));

        chapterProblemService.setProblems(ADMIN_HEADER, chapterA.getJid(), ImmutableList.of(
                new ChapterProblemData.Builder().alias("A").slug(PROBLEM_1_SLUG).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(ProblemType.PROGRAMMING).build())
        );

        chapterLessonService.setLessons(ADMIN_HEADER, chapterA.getJid(), ImmutableList.of(
                new ChapterLessonData.Builder().alias("X").slug(LESSON_1_SLUG).build(),
                new ChapterLessonData.Builder().alias("Y").slug(LESSON_2_SLUG).build())
        );

        CourseChaptersResponse response = courseChapterService.getChapters(Optional.of(ADMIN_HEADER), courseA.getJid());
        assertThat(response.getData()).containsExactly(
                new CourseChapter.Builder().alias("A").chapterJid(chapterA.getJid()).build(),
                new CourseChapter.Builder().alias("B").chapterJid(chapterB.getJid()).build());

        response = courseChapterService.getChapters(Optional.of(ADMIN_HEADER), courseB.getJid());
        assertThat(response.getData()).isEmpty();

        CourseChapterResponse chapter = courseChapterService.getChapter(Optional.empty(), courseA.getJid(), "A");
        assertThat(chapter.getJid()).isEqualTo(chapterA.getJid());
        assertThat(chapter.getName()).isEqualTo(chapterA.getName());
        assertThat(chapter.getLessonAliases()).containsExactly("X", "Y");

        CourseChapterUserProgressesResponse userProgresses = courseChapterService.getChapterUserProgresses(
                Optional.empty(),
                courseA.getJid(),
                new CourseChapterUserProgressesData.Builder().addUsernames(USER).build());

        assertThat(userProgresses.getTotalProblemsList()).containsExactly(2, 0);
        assertThat(userProgresses.getUserProgressesMap()).isEqualTo(ImmutableMap.of(USER, ImmutableList.of(0, 0)));
    }
}
