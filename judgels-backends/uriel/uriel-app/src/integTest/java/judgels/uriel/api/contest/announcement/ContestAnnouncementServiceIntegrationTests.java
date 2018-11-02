package judgels.uriel.api.contest.announcement;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.DRAFT;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.palantir.remoting.api.errors.ErrorType;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestAnnouncementServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestAnnouncementService announcementService = createService(ContestAnnouncementService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        ContestAnnouncement announcement1 = announcementService.createAnnouncement(
                ADMIN_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 1")
                        .content("this is content 1")
                        .status(PUBLISHED)
                        .build());

        assertThatRemoteExceptionThrownBy(() -> announcementService.createAnnouncement(
                USER_A_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title")
                        .content("this is content")
                        .status(PUBLISHED)
                        .build()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThat(announcement1.getUserJid()).isEqualTo(ADMIN_JID);
        assertThat(announcement1.getTitle()).isEqualTo("this is title 1");
        assertThat(announcement1.getContent()).isEqualTo("this is content 1");
        assertThat(announcement1.getStatus()).isEqualTo(PUBLISHED);

        ContestAnnouncement announcement2 = announcementService.createAnnouncement(
                ADMIN_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 2")
                        .content("this is content 2")
                        .status(PUBLISHED)
                        .build());
        ContestAnnouncement announcement3 = announcementService.createAnnouncement(
                ADMIN_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 3")
                        .content("this is content 3")
                        .status(DRAFT)
                        .build());

        ContestAnnouncementsResponse response =
                announcementService.getAnnouncements(of(ADMIN_HEADER), contest.getJid(), empty());

        ContestAnnouncementConfig config = response.getConfig();
        assertThat(config.getCanSupervise()).isTrue();

        Page<ContestAnnouncement> announcements = response.getData();
        assertThat(announcements.getPage()).containsOnly(announcement1, announcement2, announcement3);

        announcement1 = announcementService.updateAnnouncement(
                ADMIN_HEADER,
                contest.getJid(),
                announcement1.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is new title")
                        .content("this is new content")
                        .status(PUBLISHED)
                        .build());

        announcements = announcementService.getAnnouncements(of(ADMIN_HEADER), contest.getJid(), empty()).getData();
        assertThat(announcements.getPage()).containsOnly(announcement1, announcement2, announcement3);
    }
}
