package judgels.uriel.contest.file;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestFileRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestFileRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestFileRoleChecker();
    }

    @Test
    void view() {
        assertThat(checker.canView(ADMIN, contestA)).isTrue();
        assertThat(checker.canView(ADMIN, contestB)).isTrue();
        assertThat(checker.canView(ADMIN, contestC)).isTrue();

        assertThat(checker.canView(USER, contestA)).isTrue();
        assertThat(checker.canView(USER, contestB)).isFalse();
        assertThat(checker.canView(USER, contestC)).isFalse();

        assertThat(checker.canView(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canView(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canView(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canView(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canView(MANAGER, contestA)).isTrue();
        assertThat(checker.canView(MANAGER, contestB)).isTrue();
        assertThat(checker.canView(MANAGER, contestC)).isFalse();
    }
}
