package judgels.jerahmeel.api.archive;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static judgels.jerahmeel.api.archive.ArchiveErrors.SLUG_ALREADY_EXISTS;
import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.palantir.conjure.java.api.errors.ErrorType;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class ArchiveServiceIntegrationServiceTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Archive archiveA = archiveService.createArchive(ADMIN_HEADER, new ArchiveCreateData.Builder()
                .slug("archive-a")
                .name("Archive A")
                .category("Category")
                .description("This is archive A")
                .build());

        assertThat(archiveA.getSlug()).isEqualTo("archive-a");
        assertThat(archiveA.getName()).isEqualTo("Archive A");
        assertThat(archiveA.getCategory()).isEqualTo("Category");
        assertThat(archiveA.getDescription()).isEqualTo("This is archive A");

        Archive archiveB = archiveService.createArchive(ADMIN_HEADER, new ArchiveCreateData.Builder()
                .slug("archive-b")
                .name("Archive B")
                .category("Category")
                .description("This is archive B")
                .build());

        assertThat(archiveB.getSlug()).isEqualTo("archive-b");

        assertThatRemoteExceptionThrownBy(() -> archiveService
                .createArchive(ADMIN_HEADER, new ArchiveCreateData.Builder()
                        .slug("archive-a")
                        .name("A")
                        .category("C")
                        .build()))
                .isGeneratedFromErrorType(SLUG_ALREADY_EXISTS);


        assertThatRemoteExceptionThrownBy(() -> archiveService
                .updateArchive(ADMIN_HEADER, archiveB.getJid(), new ArchiveUpdateData.Builder()
                        .slug("archive-a")
                        .build()))
                .isGeneratedFromErrorType(SLUG_ALREADY_EXISTS);

        ArchivesResponse response = archiveService.getArchives(Optional.of(ADMIN_HEADER));
        assertThat(response.getData()).containsExactly(archiveA, archiveB);

        // as user


        assertThatRemoteExceptionThrownBy(() -> archiveService
                .createArchive(USER_HEADER, new ArchiveCreateData.Builder()
                        .slug("archive-c")
                        .name("Archive C")
                        .category("Category")
                        .build()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        response = archiveService.getArchives(Optional.of(USER_HEADER));
        assertThat(response.getData()).containsExactly(archiveA, archiveB);
    }
}
