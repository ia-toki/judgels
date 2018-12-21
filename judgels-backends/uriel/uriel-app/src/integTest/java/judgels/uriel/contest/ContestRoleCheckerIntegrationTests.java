package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestRoleChecker();

        moduleStore.upsertVirtualModule(
                contestB.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());
        moduleStore.upsertVirtualModule(
                contestBStarted.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());

        contestantStore.startVirtualContest(contestBStarted.getJid(), ANOTHER_CONTESTANT);
    }

    @Test
    void administer() {
        assertThat(checker.canAdminister(ADMIN)).isTrue();
        assertThat(checker.canAdminister(USER)).isFalse();
        assertThat(checker.canAdminister(CONTESTANT)).isFalse();
        assertThat(checker.canAdminister(SUPERVISOR)).isFalse();
        assertThat(checker.canAdminister(MANAGER)).isFalse();
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

    @Test
    void supervise() {
        assertThat(checker.canSupervise(ADMIN, contestA)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestB)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestC)).isTrue();

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
        assertThat(checker.canManage(ADMIN, contestC)).isTrue();

        assertThat(checker.canManage(USER, contestA)).isFalse();
        assertThat(checker.canManage(USER, contestB)).isFalse();
        assertThat(checker.canManage(USER, contestC)).isFalse();

        assertThat(checker.canManage(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canManage(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canManage(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canManage(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canManage(MANAGER, contestA)).isFalse();
        assertThat(checker.canManage(MANAGER, contestB)).isTrue();
        assertThat(checker.canManage(MANAGER, contestC)).isFalse();
    }

    @Test
    void start_virtual() {
        assertThat(checker.canStartVirtual(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canStartVirtual(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canStartVirtual(ANOTHER_CONTESTANT, contestBStarted)).isFalse();
    }

    @Test
    void get_role() {
        assertThat(checker.getRole(USER, contestB)).isEqualTo(ContestRole.NONE);
        assertThat(checker.getRole(CONTESTANT, contestB)).isEqualTo(ContestRole.CONTESTANT);
        assertThat(checker.getRole(SUPERVISOR, contestB)).isEqualTo(ContestRole.SUPERVISOR);
        assertThat(checker.getRole(MANAGER, contestB)).isEqualTo(ContestRole.MANAGER);
        assertThat(checker.getRole(ADMIN, contestB)).isEqualTo(ContestRole.ADMIN);
    }
}
