package judgels.jerahmeel.api.chapter.lesson;

import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_1_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_2_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.LESSON_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import judgels.jerahmeel.api.BaseJerahmeelServiceIntegrationTests;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import org.junit.jupiter.api.Test;

class ChapterLessonServiceIntegrationTests extends BaseJerahmeelServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterService.createChapter(adminHeader, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterService.createChapter(adminHeader, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        chapterLessonService.setLessons(adminHeader, chapterA.getJid(), ImmutableList.of(
                new ChapterLessonData.Builder().alias("A").slug(LESSON_1_SLUG).build(),
                new ChapterLessonData.Builder().alias("B").slug(LESSON_2_SLUG).build())
        );

        ChapterLessonsResponse response =
                chapterLessonService.getLessons(Optional.of(adminHeader), chapterA.getJid());

        assertThat(response.getData()).containsExactly(
                new ChapterLesson.Builder().alias("A").lessonJid(LESSON_1_JID).build(),
                new ChapterLesson.Builder().alias("B").lessonJid(LESSON_2_JID).build()
        );

        response = chapterLessonService.getLessons(Optional.of(adminHeader), chapterB.getJid());
        assertThat(response.getData()).isEmpty();

        // assert user

        assertThatThrownBy(() -> chapterLessonService
                .setLessons(userHeader, chapterA.getJid(), ImmutableList.of()))
                .hasFieldOrPropertyWithValue("code", 403);

        response = chapterLessonService.getLessons(Optional.of(userHeader), chapterA.getJid());

        assertThat(response.getData()).hasSize(2);
    }
}
