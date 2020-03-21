package judgels.uriel.contest.announcement;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ANNOUNCEMENT;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.AbstractContestRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestAnnouncementRoleCheckerIntegrationTests extends AbstractContestRoleCheckerIntegrationTests {
    private ContestAnnouncementRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestAnnouncementRoleChecker();
    }

    @Test
    void view_published() {
        assertThat(checker.canViewPublished(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewPublished(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewPublished(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewPublished(USER, contestA)).isTrue();
        assertThat(checker.canViewPublished(USER, contestB)).isFalse();
        assertThat(checker.canViewPublished(USER, contestC)).isFalse();

        assertThat(checker.canViewPublished(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canViewPublished(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewPublished(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewPublished(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canViewPublished(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewPublished(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewPublished(MANAGER, contestA)).isTrue();
        assertThat(checker.canViewPublished(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewPublished(MANAGER, contestC)).isFalse();
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
        addSupervisorToContestBWithPermission(ANNOUNCEMENT);
        assertThat(checker.canManage(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canManage(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canManage(MANAGER, contestA)).isFalse();
        assertThat(checker.canManage(MANAGER, contestB)).isTrue();
        assertThat(checker.canManage(MANAGER, contestC)).isFalse();
    }
}
