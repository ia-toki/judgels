package judgels.jerahmeel.api.chapter;

import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class ChapterServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        chapterA = chapterService.updateChapter(ADMIN_HEADER, chapterA.getJid(), new ChapterUpdateData.Builder()
                .name("Really Chapter A")
                .build());

        assertThat(chapterA.getName()).isEqualTo("Really Chapter A");

        Chapter chapterB = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        assertThat(chapterB.getName()).isEqualTo("Chapter B");

        ChaptersResponse response = chapterService.getChapters(ADMIN_HEADER);
        assertThat(response.getData()).containsExactly(chapterB, chapterA);

        // as user

        assertThatThrownBy(() -> chapterService
                .createChapter(USER_HEADER, new ChapterCreateData.Builder().name("Chapter C").build()))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(() -> chapterService
                .getChapters(USER_HEADER))
                .hasFieldOrPropertyWithValue("code", 403);
    }
}
