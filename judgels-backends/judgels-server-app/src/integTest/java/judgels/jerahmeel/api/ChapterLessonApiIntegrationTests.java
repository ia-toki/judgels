package judgels.jerahmeel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonData;
import org.junit.jupiter.api.Test;

class ChapterLessonApiIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        chapterLessonClient.setLessons(adminToken, chapterA.getJid(), List.of(
                new ChapterLessonData.Builder().alias("A").slug(LESSON_1_SLUG).build(),
                new ChapterLessonData.Builder().alias("B").slug(LESSON_2_SLUG).build()));

        var response = chapterLessonClient.getLessons(adminToken, chapterA.getJid());
        assertThat(response.getData()).containsExactly(
                new ChapterLesson.Builder().alias("A").lessonJid(lesson1.getJid()).build(),
                new ChapterLesson.Builder().alias("B").lessonJid(lesson2.getJid()).build());

        response = chapterLessonClient.getLessons(adminToken, chapterB.getJid());
        assertThat(response.getData()).isEmpty();

        // assert user

        assertForbidden(() -> chapterLessonClient
                .setLessons(userToken, chapterA.getJid(), List.of()));

        response = chapterLessonClient.getLessons(userToken, chapterA.getJid());

        assertThat(response.getData()).hasSize(2);
    }
}
