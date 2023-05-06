package judgels.jerahmeel.api.archive;

import static judgels.jerahmeel.api.archive.ArchiveErrors.SLUG_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import judgels.jerahmeel.api.BaseJerahmeelServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class ArchiveServiceIntegrationServiceTests extends BaseJerahmeelServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Archive archiveA = archiveService.createArchive(adminHeader, new ArchiveCreateData.Builder()
                .slug("archive-a")
                .name("Archive A")
                .category("Category")
                .description("This is archive A")
                .build());

        assertThat(archiveA.getSlug()).isEqualTo("archive-a");
        assertThat(archiveA.getName()).isEqualTo("Archive A");
        assertThat(archiveA.getCategory()).isEqualTo("Category");
        assertThat(archiveA.getDescription()).isEqualTo("This is archive A");

        Archive archiveB = archiveService.createArchive(adminHeader, new ArchiveCreateData.Builder()
                .slug("archive-b")
                .name("Archive B")
                .category("Category")
                .description("This is archive B")
                .build());

        assertThat(archiveB.getSlug()).isEqualTo("archive-b");

        assertThatThrownBy(() -> archiveService
                .createArchive(adminHeader, new ArchiveCreateData.Builder()
                        .slug("archive-a")
                        .name("A")
                        .category("C")
                        .build()))
                .hasFieldOrPropertyWithValue("code", 400)
                .hasMessageContaining(SLUG_ALREADY_EXISTS);


        assertThatThrownBy(() -> archiveService
                .updateArchive(adminHeader, archiveB.getJid(), new ArchiveUpdateData.Builder()
                        .slug("archive-a")
                        .build()))
                .hasFieldOrPropertyWithValue("code", 400)
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        ArchivesResponse response = archiveService.getArchives(Optional.of(adminHeader));
        assertThat(response.getData()).containsExactly(archiveA, archiveB);

        // as user


        assertThatThrownBy(() -> archiveService
                .createArchive(userHeader, new ArchiveCreateData.Builder()
                        .slug("archive-c")
                        .name("Archive C")
                        .category("Category")
                        .build()))
                .hasFieldOrPropertyWithValue("code", 403);

        response = archiveService.getArchives(Optional.of(userHeader));
        assertThat(response.getData()).containsExactly(archiveA, archiveB);
    }
}
