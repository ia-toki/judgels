package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.SCOREBOARD;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestScoreboardRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestScoreboardRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestScoreboardRoleChecker();
    }

    @Test
    void view_default_scoreboard() {
        assertThat(checker.canViewDefaultScoreboard(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewDefaultScoreboard(USER, contestA)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(USER, contestAStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(USER, contestB)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(USER, contestC)).isFalse();

        assertThat(checker.canViewDefaultScoreboard(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(CONTESTANT, contestAStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SCOREBOARD);
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewDefaultScoreboard(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewDefaultScoreboard(MANAGER, contestAStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewDefaultScoreboard(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise_scoreboard() {
        assertThat(checker.canSuperviseScoreboard(ADMIN, contestA)).isTrue();
        assertThat(checker.canSuperviseScoreboard(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canSuperviseScoreboard(ADMIN, contestB)).isTrue();
        assertThat(checker.canSuperviseScoreboard(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canSuperviseScoreboard(ADMIN, contestC)).isTrue();

        assertThat(checker.canSuperviseScoreboard(USER, contestA)).isFalse();
        assertThat(checker.canSuperviseScoreboard(USER, contestAStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(USER, contestB)).isFalse();
        assertThat(checker.canSuperviseScoreboard(USER, contestBStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(USER, contestC)).isFalse();

        assertThat(checker.canSuperviseScoreboard(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canSuperviseScoreboard(CONTESTANT, contestAStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canSuperviseScoreboard(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SCOREBOARD);
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canSuperviseScoreboard(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSuperviseScoreboard(MANAGER, contestA)).isFalse();
        assertThat(checker.canSuperviseScoreboard(MANAGER, contestAStarted)).isFalse();
        assertThat(checker.canSuperviseScoreboard(MANAGER, contestB)).isTrue();
        assertThat(checker.canSuperviseScoreboard(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canSuperviseScoreboard(MANAGER, contestC)).isFalse();
    }
}
