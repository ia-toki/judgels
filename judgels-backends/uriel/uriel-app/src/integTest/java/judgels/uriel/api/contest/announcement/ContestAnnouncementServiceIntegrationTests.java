package judgels.uriel.api.contest.announcement;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.of;
import static judgels.uriel.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.palantir.remoting.api.errors.ErrorType;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestStyle;
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
    private ContestAnnouncementService contestAnnouncementService = createService(ContestAnnouncementService.class);

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
        Contest contest = contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("TOKI Open Contest A")
                .description("This is contest A")
                .slug("contest-A")
                .style(ContestStyle.ICPC)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.ofHours(5))
                .build());

        ContestAnnouncementData announcementData1 = new ContestAnnouncementData.Builder()
                .title("this is title 1")
                .content("this is content 1")
                .status(ContestAnnouncementStatus.PUBLISHED)
                .build();

        ContestAnnouncement contestAnnouncement1 = contestAnnouncementService.createAnnouncement(
                ADMIN_HEADER, contest.getJid(), announcementData1);
        assertThatRemoteExceptionThrownBy(() -> contestAnnouncementService.createAnnouncement(
                USER_A_HEADER, contest.getJid(), announcementData1))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThat(contestAnnouncement1.getUserJid()).isEqualTo(ADMIN_JID);
        assertThat(contestAnnouncement1.getTitle()).isEqualTo("this is title 1");
        assertThat(contestAnnouncement1.getContent()).isEqualTo("this is content 1");
        assertThat(contestAnnouncement1.getStatus()).isEqualTo(ContestAnnouncementStatus.PUBLISHED);

        ContestAnnouncementData announcementData2 = new ContestAnnouncementData.Builder()
                .title("this is title 2")
                .content("this is content 2")
                .status(ContestAnnouncementStatus.PUBLISHED)
                .build();
        ContestAnnouncementData announcementData3 = new ContestAnnouncementData.Builder()
                .title("this is title 3")
                .content("this is content 3")
                .status(ContestAnnouncementStatus.DRAFT)
                .build();

        ContestAnnouncement contestAnnouncement2 = contestAnnouncementService.createAnnouncement(
                ADMIN_HEADER, contest.getJid(), announcementData2);
        ContestAnnouncement contestAnnouncement3 = contestAnnouncementService.createAnnouncement(
                ADMIN_HEADER, contest.getJid(), announcementData3);

        List<ContestAnnouncement> publishedAnnouncements = contestAnnouncementService
                .getPublishedAnnouncements(of(ADMIN_HEADER), contest.getJid());

        assertThat(publishedAnnouncements).contains(contestAnnouncement1, contestAnnouncement2);
        assertThat(publishedAnnouncements).doesNotContain(contestAnnouncement3);
    }
}
