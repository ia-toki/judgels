package judgels.jerahmeel.api.course.chapter;

import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import org.junit.jupiter.api.Test;

class CourseChapterIntegrationTests extends AbstractTrainingServiceIntegrationTests {
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

        CourseChaptersResponse response = courseChapterService.getChapters(Optional.of(ADMIN_HEADER), courseA.getJid());
        assertThat(response.getData()).containsExactly(
                new CourseChapter.Builder().alias("A").chapterJid(chapterA.getJid()).build(),
                new CourseChapter.Builder().alias("B").chapterJid(chapterB.getJid()).build());

        response = courseChapterService.getChapters(Optional.of(ADMIN_HEADER), courseB.getJid());
        assertThat(response.getData()).isEmpty();
    }
}
