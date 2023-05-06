package judgels.uriel.api.contest.announcement;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;

import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestAnnouncementServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private final ContestAnnouncementService announcementService = createService(ContestAnnouncementService.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.ANNOUNCEMENT)
                .supervisors(SUPERVISOR_B)
                .build();
    }

    @Test
    void create_update_announcement() {
        assertPermitted(createUpdateAnnouncement(adminHeader));
        assertPermitted(createUpdateAnnouncement(managerHeader));
        assertPermitted(createUpdateAnnouncement(supervisorAHeader));
        assertForbidden(createUpdateAnnouncement(supervisorBHeader));
        assertForbidden(createUpdateAnnouncement(contestantHeader));
        assertForbidden(createUpdateAnnouncement(userHeader));
    }

    @Test
    void get_announcements() {
        assertPermitted(getAnnouncements(of(adminHeader)));
        assertPermitted(getAnnouncements(of(managerHeader)));
        assertPermitted(getAnnouncements(of(supervisorAHeader)));
        assertPermitted(getAnnouncements(of(supervisorBHeader)));
        assertPermitted(getAnnouncements(of(contestantHeader)));
        assertForbidden(getAnnouncements(of(userHeader)));
        assertForbidden(getAnnouncements(empty()));

        enableModule(contest, REGISTRATION);
        assertPermitted(getAnnouncements(of(userHeader)));
        assertPermitted(getAnnouncements(empty()));
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
