package judgels.uriel.api.contest;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableList;
import com.palantir.remoting.api.errors.ErrorType;
import java.time.Duration;
import java.time.Instant;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.role.AdminRoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@WithHibernateSession(
        urlSuffix = URIEL_JDBC_SUFFIX,
        models = {AdminRoleModel.class, ContestContestantModel.class})
class ContestServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private ContestService contestService = createService(ContestService.class);
    private ContestContestantService contestantService = createService(ContestContestantService.class);

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
        Contest contestA = contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("TOKI Open Contest A")
                .description("This is contest A")
                .style(ContestStyle.ICPC)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.ofHours(5))
                .build());

        assertThat(contestA.getName()).isEqualTo("TOKI Open Contest A");
        assertThat(contestA.getDescription()).isEqualTo("This is contest A");
        assertThat(contestA.getStyle()).isEqualTo(ContestStyle.ICPC);
        assertThat(contestA.getBeginTime()).isEqualTo(Instant.ofEpochSecond(42));
        assertThat(contestA.getDuration()).isEqualTo(Duration.ofHours(5));

        assertThat(contestService.getContest(ADMIN_HEADER, contestA.getJid())).isEqualTo(contestA);
        assertThatRemoteExceptionThrownBy(
                () -> contestService.getContest(AuthHeader.of("randomToken"), contestA.getJid()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        Contest contestB = contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("TOKI Open Contest B")
                .beginTime(Instant.ofEpochSecond(43))
                .build());

        assertThat(contestService.getContest(ADMIN_HEADER, contestB.getJid())).isEqualTo(contestB);

        contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("TOKI Open Contest - Testing")
                .build());
        contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("Random Contest")
                .build());

        contestantService.addContestants(
                ADMIN_HEADER,
                contestA.getJid(),
                ImmutableList.of(USER_A_JID));
        contestantService.addContestants(
                ADMIN_HEADER,
                contestB.getJid(),
                ImmutableList.of(USER_A_JID, USER_B_JID));

        Page<Contest> contests = contestService.getContests(of(USER_A_HEADER), empty());
        assertThat(contests.getData()).containsExactly(contestB, contestA);

        contests = contestService.getContests(of(USER_B_HEADER), empty());
        assertThat(contests.getData()).containsExactly(contestB);
    }
}
