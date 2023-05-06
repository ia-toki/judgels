package judgels.jerahmeel.api.chapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import judgels.jerahmeel.api.BaseJerahmeelServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class ChapterServiceIntegrationTests extends BaseJerahmeelServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterService.createChapter(adminHeader, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        chapterA = chapterService.updateChapter(adminHeader, chapterA.getJid(), new ChapterUpdateData.Builder()
                .name("Really Chapter A")
                .build());

        assertThat(chapterA.getName()).isEqualTo("Really Chapter A");

        Chapter chapterB = chapterService.createChapter(adminHeader, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        assertThat(chapterB.getName()).isEqualTo("Chapter B");

        ChaptersResponse response = chapterService.getChapters(adminHeader);
        assertThat(response.getData()).containsExactly(chapterB, chapterA);

        // as user

        assertThatThrownBy(() -> chapterService
                .createChapter(userHeader, new ChapterCreateData.Builder().name("Chapter C").build()))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(() -> chapterService
                .getChapters(userHeader))
                .hasFieldOrPropertyWithValue("code", 403);
    }
}
