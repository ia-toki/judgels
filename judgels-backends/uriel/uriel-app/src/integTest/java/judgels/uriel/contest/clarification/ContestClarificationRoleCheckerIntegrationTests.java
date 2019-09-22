package judgels.uriel.contest.clarification;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.CLARIFICATION;
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
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestClarificationRoleChecker();

        moduleStore.upsertClarificationModule(contestA.getJid());
        moduleStore.upsertClarificationModule(contestAStarted.getJid());

        moduleStore.upsertClarificationModule(contestB.getJid());
        moduleStore.upsertClarificationModule(contestBStarted.getJid());
        moduleStore.upsertClarificationModule(contestBFinished.getJid());
    }

    @Test
    void create() {
        assertThat(checker.canCreate(ADMIN, contestA)).isFalse();
        assertThat(checker.canCreate(ADMIN, contestAStarted)).isFalse();
        assertThat(checker.canCreate(ADMIN, contestB)).isFalse();
        assertThat(checker.canCreate(ADMIN, contestBStarted)).isFalse();
        assertThat(checker.canCreate(ADMIN, contestC)).isFalse();

        assertThat(checker.canCreate(USER, contestA)).isFalse();
        assertThat(checker.canCreate(USER, contestAStarted)).isFalse();
        assertThat(checker.canCreate(USER, contestB)).isFalse();
        assertThat(checker.canCreate(USER, contestC)).isFalse();

        assertThat(checker.canCreate(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canCreate(CONTESTANT, contestB)).isFalse();

        assertThat(checker.canCreate(CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertClarificationTimeLimitModule(
                contestBStarted.getJid(),
                new ClarificationTimeLimitModuleConfig.Builder()
                        .clarificationDuration(Duration.ofHours(1))
                        .build());
        assertThat(checker.canCreate(CONTESTANT, contestBStarted)).isFalse();
        moduleStore.upsertClarificationTimeLimitModule(
                contestBStarted.getJid(),
                new ClarificationTimeLimitModuleConfig.Builder()
                        .clarificationDuration(Duration.ofHours(3))
                        .build());
        assertThat(checker.canCreate(CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canCreate(CONTESTANT, contestBStarted)).isFalse();

        assertThat(checker.canCreate(CONTESTANT, contestBFinished)).isFalse();
        assertThat(checker.canCreate(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canCreate(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canCreate(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canCreate(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canCreate(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(CLARIFICATION);
        assertThat(checker.canCreate(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canCreate(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canCreate(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canCreate(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canCreate(MANAGER, contestA)).isFalse();
        assertThat(checker.canCreate(MANAGER, contestB)).isFalse();
        assertThat(checker.canCreate(MANAGER, contestBStarted)).isFalse();
        assertThat(checker.canCreate(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_own() {
        assertThat(checker.canViewOwn(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestC)).isFalse();

        assertThat(checker.canViewOwn(USER, contestA)).isFalse();
        assertThat(checker.canViewOwn(USER, contestAStarted)).isFalse();
        assertThat(checker.canViewOwn(USER, contestB)).isFalse();
        assertThat(checker.canViewOwn(USER, contestC)).isFalse();

        assertThat(checker.canViewOwn(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewOwn(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewOwn(CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwn(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canViewOwn(CONTESTANT, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());

        assertThat(checker.canViewOwn(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwn(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwn(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewOwn(SUPERVISOR, contestC)).isFalse();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwn(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwn(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewOwn(SUPERVISOR, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());

        assertThat(checker.canViewOwn(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewOwn(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwn(MANAGER, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwn(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwn(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewOwn(MANAGER, contestC)).isFalse();
        moduleStore.disablePausedModule(contestBStarted.getJid());
    }

    @Test
    void supervise() {
        assertThat(checker.canSupervise(ADMIN, contestA)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestB)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestC)).isFalse();

        assertThat(checker.canSupervise(USER, contestA)).isFalse();
        assertThat(checker.canSupervise(USER, contestB)).isFalse();
        assertThat(checker.canSupervise(USER, contestC)).isFalse();

        assertThat(checker.canSupervise(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canSupervise(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canSupervise(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canSupervise(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSupervise(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSupervise(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSupervise(MANAGER, contestA)).isFalse();
        assertThat(checker.canSupervise(MANAGER, contestB)).isTrue();
        assertThat(checker.canSupervise(MANAGER, contestC)).isFalse();
    }

    @Test
    void manage() {
        assertThat(checker.canManage(ADMIN, contestA)).isTrue();
        assertThat(checker.canManage(ADMIN, contestB)).isTrue();
        assertThat(checker.canManage(ADMIN, contestC)).isFalse();

        assertThat(checker.canManage(USER, contestA)).isFalse();
        assertThat(checker.canManage(USER, contestB)).isFalse();
        assertThat(checker.canManage(USER, contestC)).isFalse();

        assertThat(checker.canManage(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canManage(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canManage(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canManage(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(CLARIFICATION);
        assertThat(checker.canManage(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canManage(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canManage(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canManage(MANAGER, contestA)).isFalse();
        assertThat(checker.canManage(MANAGER, contestB)).isTrue();
        assertThat(checker.canManage(MANAGER, contestC)).isFalse();
    }
}
