package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestRoleChecker checker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
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
    void create_contest() {
        assertThat(checker.canCreateContest(ADMIN)).isTrue();
        assertThat(checker.canCreateContest(USER)).isFalse();
        assertThat(checker.canCreateContest(CONTESTANT)).isFalse();
        assertThat(checker.canCreateContest(SUPERVISOR)).isFalse();
        assertThat(checker.canCreateContest(MANAGER)).isFalse();
    }

    @Test
    void view_contest() {
        assertThat(checker.canViewContest(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewContest(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewContest(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewContest(USER, contestA)).isTrue();
        assertThat(checker.canViewContest(USER, contestB)).isFalse();
        assertThat(checker.canViewContest(USER, contestC)).isFalse();

        assertThat(checker.canViewContest(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canViewContest(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewContest(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewContest(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canViewContest(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewContest(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewContest(MANAGER, contestA)).isTrue();
        assertThat(checker.canViewContest(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewContest(MANAGER, contestC)).isFalse();
    }

    @Test
    void edit_contest() {
        assertThat(checker.canEditContest(ADMIN, contestA)).isTrue();
        assertThat(checker.canEditContest(ADMIN, contestB)).isTrue();
        assertThat(checker.canEditContest(ADMIN, contestC)).isTrue();

        assertThat(checker.canEditContest(USER, contestA)).isFalse();
        assertThat(checker.canEditContest(USER, contestB)).isFalse();
        assertThat(checker.canEditContest(USER, contestC)).isFalse();

        assertThat(checker.canEditContest(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canEditContest(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canEditContest(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canEditContest(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canEditContest(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canEditContest(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canEditContest(MANAGER, contestA)).isFalse();
        assertThat(checker.canEditContest(MANAGER, contestB)).isTrue();
        assertThat(checker.canEditContest(MANAGER, contestC)).isFalse();
    }

    @Test
    void start_virtual_contest() {
        assertThat(checker.canStartVirtualContest(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canStartVirtualContest(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canStartVirtualContest(ANOTHER_CONTESTANT, contestBStarted)).isFalse();
    }
}
