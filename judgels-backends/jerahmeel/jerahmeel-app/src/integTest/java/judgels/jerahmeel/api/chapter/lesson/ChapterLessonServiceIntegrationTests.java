package judgels.jerahmeel.api.chapter.lesson;

import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_1_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_2_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import org.junit.jupiter.api.Test;

class ChapterLessonServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        chapterLessonService.setLessons(ADMIN_HEADER, chapterA.getJid(), ImmutableList.of(
                new ChapterLessonData.Builder().alias("A").slug(LESSON_1_SLUG).build(),
                new ChapterLessonData.Builder().alias("B").slug(LESSON_2_SLUG).build())
        );

        ChapterLessonsResponse response =
                chapterLessonService.getLessons(Optional.of(ADMIN_HEADER), chapterA.getJid());

        assertThat(response.getData()).containsExactly(
                new ChapterLesson.Builder().alias("A").lessonJid(LESSON_1_JID).build(),
                new ChapterLesson.Builder().alias("B").lessonJid(LESSON_2_JID).build()
        );

        response = chapterLessonService.getLessons(Optional.of(ADMIN_HEADER), chapterB.getJid());
        assertThat(response.getData()).isEmpty();

        // assert user

        assertThatThrownBy(() -> chapterLessonService
                .setLessons(USER_HEADER, chapterA.getJid(), ImmutableList.of()))
                .hasFieldOrPropertyWithValue("code", 403);

        response = chapterLessonService.getLessons(Optional.of(USER_HEADER), chapterA.getJid());

        assertThat(response.getData()).hasSize(2);
    }
}
