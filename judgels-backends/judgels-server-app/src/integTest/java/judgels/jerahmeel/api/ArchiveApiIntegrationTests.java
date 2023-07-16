package judgels.jerahmeel.api;

import static judgels.jerahmeel.api.archive.ArchiveErrors.SLUG_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.archive.ArchiveUpdateData;
import org.junit.jupiter.api.Test;

class ArchiveApiIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Archive archiveA = archiveClient.createArchive(adminToken, new ArchiveCreateData.Builder()
                .slug("archive-a")
                .name("Archive A")
                .category("Category")
                .description("This is archive A")
                .build());

        assertThat(archiveA.getSlug()).isEqualTo("archive-a");
        assertThat(archiveA.getName()).isEqualTo("Archive A");
        assertThat(archiveA.getCategory()).isEqualTo("Category");
        assertThat(archiveA.getDescription()).isEqualTo("This is archive A");

        Archive archiveB = archiveClient.createArchive(adminToken, new ArchiveCreateData.Builder()
                .slug("archive-b")
                .name("Archive B")
                .category("Category")
                .description("This is archive B")
                .build());

        assertThat(archiveB.getSlug()).isEqualTo("archive-b");

        assertBadRequest(() -> archiveClient
                .createArchive(adminToken, new ArchiveCreateData.Builder()
                        .slug("archive-a")
                        .name("A")
                        .category("C")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertBadRequest(() -> archiveClient
                .updateArchive(adminToken, archiveB.getJid(), new ArchiveUpdateData.Builder()
                        .slug("archive-a")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        var response = archiveClient.getArchives(adminToken);
        assertThat(response.getData()).containsExactly(archiveA, archiveB);

        // as user

        assertForbidden(() -> archiveClient
                .createArchive(userToken, new ArchiveCreateData.Builder()
                        .slug("archive-c")
                        .name("Archive C")
                        .category("Category")
                        .build()));

        response = archiveClient.getArchives(userToken);
        assertThat(response.getData()).containsExactly(archiveA, archiveB);
    }
}
