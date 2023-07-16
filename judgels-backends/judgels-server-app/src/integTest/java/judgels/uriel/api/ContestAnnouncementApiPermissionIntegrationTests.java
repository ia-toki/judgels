package judgels.uriel.api;

import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;

import judgels.uriel.ContestAnnouncementClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestAnnouncementApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestAnnouncementClient announcementClient = createClient(ContestAnnouncementClient.class);

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
        assertPermitted(createUpdateAnnouncement(adminToken));
        assertPermitted(createUpdateAnnouncement(managerToken));
        assertPermitted(createUpdateAnnouncement(supervisorAToken));
        assertForbidden(createUpdateAnnouncement(supervisorBToken));
        assertForbidden(createUpdateAnnouncement(contestantToken));
        assertForbidden(createUpdateAnnouncement(userToken));
    }

    @Test
    void get_announcements() {
        assertPermitted(getAnnouncements(adminToken));
        assertPermitted(getAnnouncements(managerToken));
        assertPermitted(getAnnouncements(supervisorAToken));
        assertPermitted(getAnnouncements(supervisorBToken));
        assertPermitted(getAnnouncements(contestantToken));
        assertForbidden(getAnnouncements(userToken));
        assertForbidden(getAnnouncements(""));

        enableModule(contest, REGISTRATION);
        assertPermitted(getAnnouncements(userToken));
        assertPermitted(getAnnouncements(""));
    }

    private ThrowingCallable createUpdateAnnouncement(String token) {
        ContestAnnouncementData data = new ContestAnnouncementData.Builder()
                .title(randomString())
                .content(randomString())
                .status(PUBLISHED)
                .build();
        return () -> {
            ContestAnnouncement announcement = announcementClient.createAnnouncement(token, contest.getJid(), data);
            announcementClient.updateAnnouncement(token, contest.getJid(), announcement.getJid(), data);
        };
    }

    private ThrowingCallable getAnnouncements(String token) {
        return () -> announcementClient.getAnnouncements(token, contest.getJid());
    }
}
