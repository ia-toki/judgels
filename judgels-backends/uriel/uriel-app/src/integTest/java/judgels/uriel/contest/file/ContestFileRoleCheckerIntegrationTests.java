package judgels.uriel.contest.file;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestFileRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestFileRoleChecker checker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestFileRoleChecker();
    }

    @Test
    void download_files() {
        assertThat(checker.canDownloadFiles(ADMIN, contestA)).isTrue();
        assertThat(checker.canDownloadFiles(ADMIN, contestB)).isTrue();
        assertThat(checker.canDownloadFiles(ADMIN, contestC)).isTrue();

        assertThat(checker.canDownloadFiles(USER, contestA)).isTrue();
        assertThat(checker.canDownloadFiles(USER, contestB)).isFalse();
        assertThat(checker.canDownloadFiles(USER, contestC)).isFalse();

        assertThat(checker.canDownloadFiles(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canDownloadFiles(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canDownloadFiles(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canDownloadFiles(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canDownloadFiles(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canDownloadFiles(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canDownloadFiles(MANAGER, contestA)).isTrue();
        assertThat(checker.canDownloadFiles(MANAGER, contestB)).isTrue();
        assertThat(checker.canDownloadFiles(MANAGER, contestC)).isFalse();
    }
}
