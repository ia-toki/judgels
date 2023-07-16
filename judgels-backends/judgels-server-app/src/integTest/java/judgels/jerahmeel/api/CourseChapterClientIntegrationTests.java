package judgels.jerahmeel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.jerahmeel.api.course.chapter.CourseChapter;
import judgels.jerahmeel.api.course.chapter.CourseChapterResponse;
import judgels.jerahmeel.api.course.chapter.CourseChapterUserProgressesData;
import judgels.jerahmeel.api.course.chapter.CourseChapterUserProgressesResponse;
import judgels.sandalphon.api.problem.ProblemType;
import org.junit.jupiter.api.Test;

class CourseChapterClientIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Course courseA = courseClient.createCourse(adminToken, new CourseCreateData.Builder()
                .slug("course-a")
                .name("Course A")
                .build());
        Course courseB = courseClient.createCourse(adminToken, new CourseCreateData.Builder()
                .slug("course-b")
                .name("Course B")
                .build());

        Chapter chapterA = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());
        chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter C")
                .build());

        courseChapterClient.setChapters(adminToken, courseA.getJid(), List.of(
                new CourseChapter.Builder().alias("A").chapterJid(chapterA.getJid()).build(),
                new CourseChapter.Builder().alias("B").chapterJid(chapterB.getJid()).build()));

        chapterProblemClient.setProblems(adminToken, chapterA.getJid(), List.of(
                new ChapterProblemData.Builder().alias("A").slug(PROBLEM_1_SLUG).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(ProblemType.PROGRAMMING).build()));

        chapterLessonClient.setLessons(adminToken, chapterA.getJid(), List.of(
                new ChapterLessonData.Builder().alias("X").slug(LESSON_1_SLUG).build(),
                new ChapterLessonData.Builder().alias("Y").slug(LESSON_2_SLUG).build()));

        var response = courseChapterClient.getChapters(adminToken, courseA.getJid());
        assertThat(response.getData()).containsExactly(
                new CourseChapter.Builder().alias("A").chapterJid(chapterA.getJid()).build(),
                new CourseChapter.Builder().alias("B").chapterJid(chapterB.getJid()).build());

        response = courseChapterClient.getChapters(adminToken, courseB.getJid());
        assertThat(response.getData()).isEmpty();

        CourseChapterResponse chapter = courseChapterClient.getChapter("", courseA.getJid(), "A");
        assertThat(chapter.getJid()).isEqualTo(chapterA.getJid());
        assertThat(chapter.getName()).isEqualTo(chapterA.getName());
        assertThat(chapter.getLessonAliases()).containsExactly("X", "Y");

        CourseChapterUserProgressesResponse userProgresses = courseChapterClient.getChapterUserProgresses(
                "",
                courseA.getJid(),
                new CourseChapterUserProgressesData.Builder().addUsernames(USER).build());

        assertThat(userProgresses.getTotalProblemsList()).containsExactly(2, 0);
        assertThat(userProgresses.getUserProgressesMap()).isEqualTo(Map.of(USER, List.of(0, 0)));
    }
}
