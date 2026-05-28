package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tlx.api.chapter.Chapter;
import tlx.api.chapter.ChapterCreateData;
import tlx.api.chapter.ChapterUpdateData;

class ChapterApiIntegrationTests extends BaseTrainingApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterAdminClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        chapterA = chapterAdminClient.updateChapter(adminToken, chapterA.getJid(), new ChapterUpdateData.Builder()
                .name("Really Chapter A")
                .build());

        assertThat(chapterA.getName()).isEqualTo("Really Chapter A");

        Chapter chapterB = chapterAdminClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        assertThat(chapterB.getName()).isEqualTo("Chapter B");

        var response = chapterAdminClient.getChapters(adminToken);
        assertThat(response.getData()).containsExactly(chapterB, chapterA);

        // as user

        assertForbidden(() -> chapterAdminClient
                .createChapter(userToken, new ChapterCreateData.Builder().name("Chapter C").build()));

        assertForbidden(() -> chapterAdminClient
                .getChapters(userToken));
    }
}
