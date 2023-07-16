package judgels.uriel.api;

import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.DRAFT;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.uriel.ContestAnnouncementClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestAnnouncementApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestAnnouncementClient announcementClient = createClient(ContestAnnouncementClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.ANNOUNCEMENT)
                .supervisors(SUPERVISOR_B)
                .contestants(CONTESTANT_A, CONTESTANT_B)
                .modules(REGISTRATION)
                .build();
    }

    @Test
    void create_update_announcement() {
        ContestAnnouncement announcement = announcementClient.createAnnouncement(
                supervisorAToken,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 1")
                        .content("this is content 1")
                        .status(PUBLISHED)
                        .build());

        assertThat(announcement.getUserJid()).isEqualTo(supervisorA.getJid());
        assertThat(announcement.getTitle()).isEqualTo("this is title 1");
        assertThat(announcement.getContent()).isEqualTo("this is content 1");
        assertThat(announcement.getStatus()).isEqualTo(PUBLISHED);

        announcement = announcementClient.updateAnnouncement(
                managerToken,
                contest.getJid(),
                announcement.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is new title")
                        .content("this is new content")
                        .status(DRAFT)
                        .build());

        assertThat(announcement.getUserJid()).isEqualTo(supervisorA.getJid());
        assertThat(announcement.getTitle()).isEqualTo("this is new title");
        assertThat(announcement.getContent()).isEqualTo("this is new content");
        assertThat(announcement.getStatus()).isEqualTo(DRAFT);
    }

    @Test
    void get_announcements() {
        ContestAnnouncement announcement1 = createAnnouncement(PUBLISHED);
        ContestAnnouncement announcement2 = createAnnouncement(DRAFT);
        ContestAnnouncement announcement3 = createAnnouncement(PUBLISHED);

        Map<String, List<ContestAnnouncement>> announcementsMap = new LinkedHashMap<>();
        announcementsMap.put(adminToken, List.of(announcement3, announcement2, announcement1));
        announcementsMap.put(managerToken, List.of(announcement3, announcement2, announcement1));
        announcementsMap.put(supervisorAToken, List.of(announcement3, announcement2, announcement1));
        announcementsMap.put(supervisorBToken, List.of(announcement3, announcement2, announcement1));
        announcementsMap.put(contestantToken, List.of(announcement3, announcement1));
        announcementsMap.put(userToken, List.of(announcement3, announcement1));
        announcementsMap.put("", List.of(announcement3, announcement1));

        Map<String, Boolean> canSuperviseMap = new LinkedHashMap<>();
        canSuperviseMap.put(adminToken, true);
        canSuperviseMap.put(managerToken, true);
        canSuperviseMap.put(supervisorAToken, true);
        canSuperviseMap.put(supervisorBToken, true);
        canSuperviseMap.put(contestantToken, false);
        canSuperviseMap.put(userToken, false);
        canSuperviseMap.put("", false);

        Map<String, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminToken, true);
        canManageMap.put(managerToken, true);
        canManageMap.put(supervisorAToken, true);
        canManageMap.put(supervisorBToken, false);
        canManageMap.put(contestantToken, false);
        canManageMap.put(userToken, false);
        canManageMap.put("", false);

        for (String authToken : announcementsMap.keySet()) {
            var response = announcementClient.getAnnouncements(authToken, contest.getJid());
            assertThat(response.getData().getPage()).containsExactlyElementsOf(announcementsMap.get(authToken));
            assertThat(response.getProfilesMap()).containsOnlyKeys(supervisorA.getJid());
            assertThat(response.getConfig().getCanSupervise()).isEqualTo(canSuperviseMap.get(authToken));
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authToken));
        }
    }

    private ContestAnnouncement createAnnouncement(ContestAnnouncementStatus status) {
        return announcementClient.createAnnouncement(
                supervisorAToken,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title(randomString())
                        .content(randomString())
                        .status(status)
                        .build());
    }
}
