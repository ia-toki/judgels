package judgels.uriel.contest.clarification;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.CLARIFICATION;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestClarificationRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestClarificationRoleChecker checker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestClarificationRoleChecker();

        moduleStore.upsertClarificationModule(contestA.getJid());
        moduleStore.upsertClarificationModule(contestAStarted.getJid());

        moduleStore.upsertClarificationModule(contestB.getJid());
        moduleStore.upsertClarificationModule(contestBStarted.getJid());
        moduleStore.upsertClarificationModule(contestBFinished.getJid());
    }

    @Test
    void view_own_clarifications() {
        assertThat(checker.canViewOwnClarifications(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewOwnClarifications(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewOwnClarifications(USER, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(USER, contestAStarted)).isFalse();
        assertThat(checker.canViewOwnClarifications(USER, contestB)).isFalse();
        assertThat(checker.canViewOwnClarifications(USER, contestC)).isFalse();

        assertThat(checker.canViewOwnClarifications(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewOwnClarifications(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(CLARIFICATION);
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewOwnClarifications(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(MANAGER, contestC)).isFalse();
    }
}
