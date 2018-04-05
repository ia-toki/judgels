package judgels.uriel.api.contest;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
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
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.hibernate.AdminRoleHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.role.RoleStore;
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
        AdminRoleDao adminRoleDao = new AdminRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider()) {};
        ContestContestantDao contestantDao = new ContestContestantHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        RoleStore roleStore = new RoleStore(adminRoleDao, contestantDao);
        roleStore.addAdmin(ADMIN_JID);
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
                .build());

        assertThat(contestA.getName()).isEqualTo("TOKI Open Contest A");
        assertThat(contestA.getDescription()).isEqualTo("This is contest A");
        assertThat(contestA.getStyle()).isEqualTo(ContestStyle.ICPC);

        assertThat(contestService.getContest(ADMIN_HEADER, contestA.getJid())).isEqualTo(contestA);
        assertThatRemoteExceptionThrownBy(
                () -> contestService.getContest(AuthHeader.of("randomToken"), contestA.getJid()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        Contest contestB = contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("TOKI Open Contest B")
                .description("This is contest B")
                .style(ContestStyle.IOI)
                .build());

        assertThat(contestService.getContest(ADMIN_HEADER, contestB.getJid())).isEqualTo(contestB);

        contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("TOKI Open Contest - Testing")
                .description("This is testing contest")
                .style(ContestStyle.IOI)
                .build());
        contestService.createContest(ADMIN_HEADER, new ContestData.Builder()
                .name("Random Contest")
                .description("This is random contest")
                .style(ContestStyle.IOI)
                .build());

        contestantService.addContestants(
                ADMIN_HEADER,
                contestA.getJid(),
                ImmutableList.of(USER_A_JID));
        contestantService.addContestants(
                ADMIN_HEADER,
                contestB.getJid(),
                ImmutableList.of(USER_A_JID, USER_B_JID));

        Page<Contest> contests = contestService.getContests(USER_A_HEADER, empty());
        assertThat(contests.getData()).containsExactly(contestB, contestA);

        contests = contestService.getContests(USER_B_HEADER, empty());
        assertThat(contests.getData()).containsExactly(contestB);
    }
}
