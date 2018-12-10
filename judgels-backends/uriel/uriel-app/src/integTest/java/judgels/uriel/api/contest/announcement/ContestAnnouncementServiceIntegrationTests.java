package judgels.uriel.api.contest.announcement;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.DRAFT;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.palantir.conjure.java.api.errors.ErrorType;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestAnnouncementServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestAnnouncementService announcementService = createService(ContestAnnouncementService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        // as manager

        ContestAnnouncement announcement1 = announcementService.createAnnouncement(
                MANAGER_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 1")
                        .content("this is content 1")
                        .status(PUBLISHED)
                        .build());

        assertThat(announcement1.getUserJid()).isEqualTo(MANAGER_JID);
        assertThat(announcement1.getTitle()).isEqualTo("this is title 1");
        assertThat(announcement1.getContent()).isEqualTo("this is content 1");
        assertThat(announcement1.getStatus()).isEqualTo(PUBLISHED);

        ContestAnnouncement announcement2 = announcementService.createAnnouncement(
                MANAGER_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 2")
                        .content("this is content 2")
                        .status(PUBLISHED)
                        .build());
        ContestAnnouncement announcement3 = announcementService.createAnnouncement(
                MANAGER_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 3")
                        .content("this is content 3")
                        .status(PUBLISHED)
                        .build());

        ContestAnnouncementsResponse response =
                announcementService.getAnnouncements(of(MANAGER_HEADER), contest.getJid(), empty());

        Page<ContestAnnouncement> announcements = response.getData();
        assertThat(announcements.getPage()).containsOnly(announcement1, announcement2, announcement3);

        ContestAnnouncementConfig config = response.getConfig();
        assertThat(config.getCanSupervise()).isTrue();
        assertThat(config.getCanManage()).isTrue();

        announcement1 = announcementService.updateAnnouncement(
                MANAGER_HEADER,
                contest.getJid(),
                announcement1.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is new title")
                        .content("this is new content")
                        .status(DRAFT)
                        .build());

        announcements = announcementService.getAnnouncements(of(MANAGER_HEADER), contest.getJid(), empty()).getData();
        assertThat(announcements.getPage()).containsOnly(announcement1, announcement2, announcement3);

        // as supervisor

        response = announcementService.getAnnouncements(of(SUPERVISOR_HEADER), contest.getJid(), empty());

        announcements = response.getData();
        assertThat(announcements.getPage()).containsOnly(announcement1, announcement2, announcement3);

        config = response.getConfig();
        assertThat(config.getCanSupervise()).isTrue();
        assertThat(config.getCanManage()).isFalse();

        assertThatRemoteExceptionThrownBy(() -> announcementService.createAnnouncement(
                SUPERVISOR_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title")
                        .content("this is content")
                        .status(PUBLISHED)
                        .build()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThatRemoteExceptionThrownBy(() -> announcementService.updateAnnouncement(
                SUPERVISOR_HEADER,
                contest.getJid(),
                announcement2.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title")
                        .content("this is content")
                        .status(PUBLISHED)
                        .build()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        // as contestant

        response = announcementService.getAnnouncements(of(CONTESTANT_HEADER), contest.getJid(), empty());

        announcements = response.getData();
        assertThat(announcements.getPage()).containsOnly(announcement2, announcement3);

        config = response.getConfig();
        assertThat(config.getCanSupervise()).isFalse();
        assertThat(config.getCanManage()).isFalse();
    }
}
