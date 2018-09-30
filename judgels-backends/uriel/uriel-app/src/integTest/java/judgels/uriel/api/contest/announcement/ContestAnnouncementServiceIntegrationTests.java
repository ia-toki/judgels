package judgels.uriel.api.contest.announcement;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.of;
import static judgels.uriel.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
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
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
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
    static void startMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
    }

    @BeforeAll
    static void setUpRoles(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        AdminRoleStore adminRoleStore = component.adminRoleStore();
        adminRoleStore.addAdmin(ADMIN_JID);
    }

    @AfterAll
    static void shutdownMocks() {
        mockJophiel.shutdown();
    }

    @Test
    void basic_flow() {
        Contest contest = contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug("contest").build());

        ContestAnnouncementData announcementData1 = new ContestAnnouncementData.Builder()
                .title("this is title 1")
                .content("this is content 1")
                .status(PUBLISHED)
                .build();

        ContestAnnouncement announcement1 = announcementService.createAnnouncement(
                ADMIN_HEADER,
                contest.getJid(),
                announcementData1);

        assertThatRemoteExceptionThrownBy(() -> announcementService.createAnnouncement(
                USER_A_HEADER,
                contest.getJid(),
                announcementData1))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThat(announcementService.getAnnouncementConfig(of(ADMIN_HEADER), contest.getJid())
                .getIsAllowedToCreateAnnouncement()).isTrue();
        assertThat(announcementService.getAnnouncementConfig(of(USER_A_HEADER), contest.getJid())
                .getIsAllowedToCreateAnnouncement()).isFalse();

        assertThat(announcement1.getUserJid()).isEqualTo(ADMIN_JID);
        assertThat(announcement1.getTitle()).isEqualTo("this is title 1");
        assertThat(announcement1.getContent()).isEqualTo("this is content 1");
        assertThat(announcement1.getStatus()).isEqualTo(PUBLISHED);

        ContestAnnouncementData announcementData2 = new ContestAnnouncementData.Builder()
                .title("this is title 2")
                .content("this is content 2")
                .status(PUBLISHED)
                .build();
        ContestAnnouncementData announcementData3 = new ContestAnnouncementData.Builder()
                .title("this is title 3")
                .content("this is content 3")
                .status(DRAFT)
                .build();

        ContestAnnouncement announcement2 = announcementService.createAnnouncement(
                ADMIN_HEADER, contest.getJid(), announcementData2);
        ContestAnnouncement announcement3 = announcementService.createAnnouncement(
                ADMIN_HEADER, contest.getJid(), announcementData3);

        List<ContestAnnouncement> allAnnouncements = announcementService
                .getAnnouncements(of(ADMIN_HEADER), contest.getJid());
        assertThat(allAnnouncements).containsOnly(announcement1, announcement2, announcement3);
    }
}
