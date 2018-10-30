package judgels.uriel.api.contest.announcement;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.of;
import static judgels.uriel.api.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.DRAFT;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.palantir.remoting.api.errors.ErrorType;
import java.util.List;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.role.AdminRoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@WithHibernateSession(
        urlSuffix = URIEL_JDBC_SUFFIX,
        models = {AdminRoleModel.class, ContestModel.class, ContestAnnouncementModel.class})
class ContestAnnouncementServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private ContestService contestService = createService(ContestService.class);
    private ContestAnnouncementService announcementService = createService(ContestAnnouncementService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
    }

    @BeforeAll
    static void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        AdminRoleStore adminRoleStore = component.adminRoleStore();
        adminRoleStore.addAdmin(ADMIN_JID);
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
    }

    @Test
    void end_to_end_flow() {
        Contest contest = contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug("contest").build());

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

        ContestAnnouncementsResponse response = announcementService
                .getAnnouncements(of(ADMIN_HEADER), contest.getJid());

        ContestAnnouncementConfig config = response.getConfig();
        assertThat(config.getCanSupervise()).isTrue();

        List<ContestAnnouncement> announcements = response.getData();
        assertThat(announcements).containsOnly(announcement1, announcement2, announcement3);

        ContestAnnouncement announcement4 = announcementService.updateAnnouncement(
                ADMIN_HEADER,
                contest.getJid(),
                announcement1.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is new title")
                        .content("this is new content")
                        .status(PUBLISHED)
                        .build());

        announcements = announcementService
                .getAnnouncements(of(ADMIN_HEADER), contest.getJid()).getData();

        assertThat(announcements.stream().filter(
                e -> e.getJid().equals(announcement1.getJid())).toArray()).containsOnly(announcement4);
    }
}
