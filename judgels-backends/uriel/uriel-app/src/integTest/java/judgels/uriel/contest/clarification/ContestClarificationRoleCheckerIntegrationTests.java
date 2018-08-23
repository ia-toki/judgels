package judgels.uriel.contest.clarification;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.CLARIFICATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
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
    void create_clarification() {
        assertThat(checker.canCreateClarification(ADMIN, contestA)).isFalse();
        assertThat(checker.canCreateClarification(ADMIN, contestAStarted)).isFalse();
        assertThat(checker.canCreateClarification(ADMIN, contestB)).isFalse();
        assertThat(checker.canCreateClarification(ADMIN, contestBStarted)).isFalse();
        assertThat(checker.canCreateClarification(ADMIN, contestC)).isFalse();

        assertThat(checker.canCreateClarification(USER, contestA)).isFalse();
        assertThat(checker.canCreateClarification(USER, contestAStarted)).isFalse();
        assertThat(checker.canCreateClarification(USER, contestB)).isFalse();
        assertThat(checker.canCreateClarification(USER, contestC)).isFalse();

        assertThat(checker.canCreateClarification(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canCreateClarification(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestA)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestB)).isFalse();

        assertThat(checker.canCreateClarification(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertClarificationTimeLimitModule(
                contestBStarted.getJid(),
                new ClarificationTimeLimitModuleConfig.Builder()
                        .clarificationDuration(Duration.ofHours(1))
                        .build());
        assertThat(checker.canCreateClarification(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestBStarted)).isFalse();
        moduleStore.upsertClarificationTimeLimitModule(
                contestBStarted.getJid(),
                new ClarificationTimeLimitModuleConfig.Builder()
                        .clarificationDuration(Duration.ofHours(3))
                        .build());
        assertThat(checker.canCreateClarification(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canCreateClarification(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestBStarted)).isFalse();

        assertThat(checker.canCreateClarification(CONTESTANT, contestBFinished)).isFalse();
        assertThat(checker.canCreateClarification(CONTESTANT, contestC)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestBFinished)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR_CONTESTANT, contestC)).isFalse();

        assertThat(checker.canCreateClarification(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(CLARIFICATION);
        assertThat(checker.canCreateClarification(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canCreateClarification(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canCreateClarification(MANAGER, contestA)).isFalse();
        assertThat(checker.canCreateClarification(MANAGER, contestB)).isFalse();
        assertThat(checker.canCreateClarification(MANAGER, contestBStarted)).isFalse();
        assertThat(checker.canCreateClarification(MANAGER, contestC)).isFalse();
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
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwnClarifications(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canViewOwnClarifications(CONTESTANT, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());

        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestC)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestC)).isFalse()
        addSupervisorToContestBWithPermission(CLARIFICATION);
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR, contestC)).isFalse();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(SUPERVISOR_CONTESTANT, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());

        assertThat(checker.canViewOwnClarifications(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewOwnClarifications(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(MANAGER, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwnClarifications(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwnClarifications(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnClarifications(MANAGER, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());
    }

    @Test
    void view_all_clarifications() {
        assertThat(checker.canViewAllClarifications(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewAllClarifications(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewAllClarifications(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewAllClarifications(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewAllClarifications(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewAllClarifications(USER, contestA)).isFalse();
        assertThat(checker.canViewAllClarifications(USER, contestAStarted)).isFalse();
        assertThat(checker.canViewAllClarifications(USER, contestB)).isFalse();
        assertThat(checker.canViewAllClarifications(USER, contestC)).isFalse();

        assertThat(checker.canViewAllClarifications(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewAllClarifications(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewAllClarifications(CONTESTANT, contestBStarted)).isFalse();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewAllClarifications(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canViewAllClarifications(CONTESTANT, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());

        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestC)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestC)).isFalse();
        addSupervisorToContestBWithPermission(CLARIFICATION);
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewAllClarifications(SUPERVISOR, contestC)).isFalse();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canViewAllClarifications(SUPERVISOR_CONTESTANT, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());

        assertThat(checker.canViewAllClarifications(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewAllClarifications(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewAllClarifications(MANAGER, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewAllClarifications(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewAllClarifications(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewAllClarifications(MANAGER, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());
    }
}
