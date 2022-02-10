package judgels.uriel.api.contest.announcement;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ANNOUNCEMENT;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;

import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestAnnouncementServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestAnnouncementService announcementService = createService(ContestAnnouncementService.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
        contest = beginContest(contest);
    }

    @Test
    void create_update_announcement() {
        assertPermitted(createUpdateAnnouncement(ADMIN_HEADER));
        assertPermitted(createUpdateAnnouncement(MANAGER_HEADER));
        assertForbidden(createUpdateAnnouncement(SUPERVISOR_HEADER));
        assertForbidden(createUpdateAnnouncement(CONTESTANT_HEADER));
        assertForbidden(createUpdateAnnouncement(USER_HEADER));

        upsertSupervisorWithPermission(contest, ANNOUNCEMENT);
        assertPermitted(createUpdateAnnouncement(SUPERVISOR_HEADER));
    }

    @Test
    void get_announcements() {
        assertPermitted(getAnnouncements(of(ADMIN_HEADER)));
        assertPermitted(getAnnouncements(of(MANAGER_HEADER)));
        assertPermitted(getAnnouncements(of(SUPERVISOR_HEADER)));
        assertPermitted(getAnnouncements(of(CONTESTANT_HEADER)));
        assertForbidden(getAnnouncements(of(USER_HEADER)));
        assertForbidden(getAnnouncements(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getAnnouncements(of(ADMIN_HEADER)));
        assertPermitted(getAnnouncements(of(MANAGER_HEADER)));
        assertPermitted(getAnnouncements(of(SUPERVISOR_HEADER)));
        assertPermitted(getAnnouncements(of(CONTESTANT_HEADER)));
        assertPermitted(getAnnouncements(of(USER_HEADER)));
        assertPermitted(getAnnouncements(empty()));

        contest = createContest();

        assertPermitted(getAnnouncements(of(ADMIN_HEADER)));
        assertForbidden(getAnnouncements(of(MANAGER_HEADER)));
        assertForbidden(getAnnouncements(of(SUPERVISOR_HEADER)));
        assertForbidden(getAnnouncements(of(CONTESTANT_HEADER)));
        assertForbidden(getAnnouncements(of(USER_HEADER)));
        assertForbidden(getAnnouncements(empty()));
    }

    private ThrowingCallable createUpdateAnnouncement(AuthHeader authHeader) {
        ContestAnnouncementData data = new ContestAnnouncementData.Builder()
                .title(randomString())
                .content(randomString())
                .status(PUBLISHED)
                .build();
        return () -> {
            ContestAnnouncement announcement =
                    announcementService.createAnnouncement(authHeader, contest.getJid(), data);
            announcementService.updateAnnouncement(authHeader, contest.getJid(), announcement.getJid(), data);
        };
    }

    private ThrowingCallable getAnnouncements(Optional<AuthHeader> authHeader) {
        return () -> announcementService.getAnnouncements(authHeader, contest.getJid(), Optional.empty());
    }
}
