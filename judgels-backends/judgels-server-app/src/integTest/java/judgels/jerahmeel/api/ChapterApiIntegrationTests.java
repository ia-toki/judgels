package judgels.jerahmeel.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.ChapterUpdateData;
import org.junit.jupiter.api.Test;

class ChapterApiIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        chapterA = chapterClient.updateChapter(adminToken, chapterA.getJid(), new ChapterUpdateData.Builder()
                .name("Really Chapter A")
                .build());

        assertThat(chapterA.getName()).isEqualTo("Really Chapter A");

        Chapter chapterB = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        assertThat(chapterB.getName()).isEqualTo("Chapter B");

        var response = chapterClient.getChapters(adminToken);
        assertThat(response.getData()).containsExactly(chapterB, chapterA);

        // as user

        assertForbidden(() -> chapterClient
                .createChapter(userToken, new ChapterCreateData.Builder().name("Chapter C").build()));

        assertForbidden(() -> chapterClient
                .getChapters(userToken));
    }
}
