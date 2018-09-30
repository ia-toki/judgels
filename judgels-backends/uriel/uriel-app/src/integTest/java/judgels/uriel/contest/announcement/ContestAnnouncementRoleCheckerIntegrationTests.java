package judgels.uriel.contest.announcement;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.ANNOUNCEMENT;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestAnnouncementRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestAnnouncementRoleChecker checker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestAnnouncementRoleChecker();
    }

    @Test
    void create_announcement() {
        assertThat(checker.canCreateAnnouncement(ADMIN, contestA)).isTrue();
        assertThat(checker.canCreateAnnouncement(ADMIN, contestB)).isTrue();
        assertThat(checker.canCreateAnnouncement(ADMIN, contestC)).isTrue();

        assertThat(checker.canCreateAnnouncement(USER, contestA)).isFalse();
        assertThat(checker.canCreateAnnouncement(USER, contestB)).isFalse();
        assertThat(checker.canCreateAnnouncement(USER, contestC)).isFalse();

        assertThat(checker.canCreateAnnouncement(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canCreateAnnouncement(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canCreateAnnouncement(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canCreateAnnouncement(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canCreateAnnouncement(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canCreateAnnouncement(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(ANNOUNCEMENT);
        assertThat(checker.canCreateAnnouncement(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canCreateAnnouncement(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canCreateAnnouncement(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canCreateAnnouncement(MANAGER, contestA)).isFalse();
        assertThat(checker.canCreateAnnouncement(MANAGER, contestB)).isTrue();
        assertThat(checker.canCreateAnnouncement(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_published_announcements() {
        assertThat(checker.canViewPublishedAnnouncements(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewPublishedAnnouncements(USER, contestA)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(USER, contestB)).isFalse();
        assertThat(checker.canViewPublishedAnnouncements(USER, contestC)).isFalse();

        assertThat(checker.canViewPublishedAnnouncements(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewPublishedAnnouncements(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(ANNOUNCEMENT);
        assertThat(checker.canViewPublishedAnnouncements(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewPublishedAnnouncements(MANAGER, contestA)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewPublishedAnnouncements(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_all_announcements() {
        assertThat(checker.canViewAllAnnouncements(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewAllAnnouncements(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewAllAnnouncements(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewAllAnnouncements(USER, contestA)).isFalse();
        assertThat(checker.canViewAllAnnouncements(USER, contestB)).isFalse();
        assertThat(checker.canViewAllAnnouncements(USER, contestC)).isFalse();

        assertThat(checker.canViewAllAnnouncements(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewAllAnnouncements(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewAllAnnouncements(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewAllAnnouncements(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewAllAnnouncements(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewAllAnnouncements(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewAllAnnouncements(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewAllAnnouncements(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewAllAnnouncements(MANAGER, contestC)).isFalse();
    }
}
