package judgels.uriel.contest.contestant;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestContestantRoleChecker checker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestContestantRoleChecker();
    }

    @Test
    void add_contestants() {
        assertThat(checker.canAddContestants(ADMIN, contestA)).isTrue();
        assertThat(checker.canAddContestants(ADMIN, contestB)).isTrue();
        assertThat(checker.canAddContestants(ADMIN, contestC)).isTrue();

        assertThat(checker.canAddContestants(USER, contestA)).isFalse();
        assertThat(checker.canAddContestants(USER, contestB)).isFalse();
        assertThat(checker.canAddContestants(USER, contestC)).isFalse();

        assertThat(checker.canAddContestants(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canAddContestants(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canAddContestants(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canAddContestants(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canAddContestants(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canAddContestants(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canAddContestants(MANAGER, contestA)).isFalse();
        assertThat(checker.canAddContestants(MANAGER, contestB)).isTrue();
        assertThat(checker.canAddContestants(MANAGER, contestC)).isFalse();
    }
}
