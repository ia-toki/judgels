package judgels.uriel.api.contest.announcement;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.DRAFT;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestAnnouncementServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
    private final ContestAnnouncementService announcementService = createService(ContestAnnouncementService.class);

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
        ContestAnnouncement announcement = announcementService.createAnnouncement(
                supervisorAHeader,
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

        announcement = announcementService.updateAnnouncement(
                managerHeader,
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

        Map<Optional<AuthHeader>, List<ContestAnnouncement>> announcementsMap = new LinkedHashMap<>();
        announcementsMap.put(of(adminHeader), ImmutableList.of(announcement3, announcement2, announcement1));
        announcementsMap.put(of(managerHeader), ImmutableList.of(announcement3, announcement2, announcement1));
        announcementsMap.put(of(supervisorAHeader), ImmutableList.of(announcement3, announcement2, announcement1));
        announcementsMap.put(of(supervisorBHeader), ImmutableList.of(announcement3, announcement2, announcement1));
        announcementsMap.put(of(contestantHeader), ImmutableList.of(announcement3, announcement1));
        announcementsMap.put(of(userHeader), ImmutableList.of(announcement3, announcement1));
        announcementsMap.put(empty(), ImmutableList.of(announcement3, announcement1));

        Map<Optional<AuthHeader>, Boolean> canSuperviseMap = new LinkedHashMap<>();
        canSuperviseMap.put(of(adminHeader), true);
        canSuperviseMap.put(of(managerHeader), true);
        canSuperviseMap.put(of(supervisorAHeader), true);
        canSuperviseMap.put(of(supervisorBHeader), true);
        canSuperviseMap.put(of(contestantHeader), false);
        canSuperviseMap.put(of(userHeader), false);
        canSuperviseMap.put(empty(), false);

        Map<Optional<AuthHeader>, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(of(adminHeader), true);
        canManageMap.put(of(managerHeader), true);
        canManageMap.put(of(supervisorAHeader), true);
        canManageMap.put(of(supervisorBHeader), false);
        canManageMap.put(of(contestantHeader), false);
        canManageMap.put(of(userHeader), false);
        canManageMap.put(empty(), false);

        for (Optional<AuthHeader> authHeader : announcementsMap.keySet()) {
            ContestAnnouncementsResponse response =
                    announcementService.getAnnouncements(authHeader, contest.getJid(), empty());
            assertThat(response.getData().getPage()).containsExactlyElementsOf(announcementsMap.get(authHeader));
            assertThat(response.getProfilesMap()).containsOnlyKeys(supervisorA.getJid());
            assertThat(response.getConfig().getCanSupervise()).isEqualTo(canSuperviseMap.get(authHeader));
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authHeader));
        }
    }

    private ContestAnnouncement createAnnouncement(ContestAnnouncementStatus status) {
        return announcementService.createAnnouncement(
                supervisorAHeader,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title(randomString())
                        .content(randomString())
                        .status(status)
                        .build());
    }
}
