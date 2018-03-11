package judgels.uriel.contest;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import com.palantir.remoting.api.errors.ErrorType;
import javax.ws.rs.core.HttpHeaders;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.contest.contestant.ContestContestantDao;
import judgels.uriel.contest.contestant.ContestContestantModel;
import judgels.uriel.hibernate.AdminRoleHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.role.AdminRoleDao;
import judgels.uriel.role.AdminRoleModel;
import judgels.uriel.role.RoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@WithHibernateSession(
        urlSuffix = AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX,
        models = {AdminRoleModel.class, ContestContestantModel.class})
class ContestServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static final String ADMIN_BEARER_TOKEN = "adminToken";
    private static final AuthHeader ADMIN_HEADER = AuthHeader.of(ADMIN_BEARER_TOKEN);
    private static final int JOPHIEL_PORT = 9001;
    private static WireMockServer wireMockServer;
    private ContestService contestService = createService(ContestService.class);

    @BeforeAll static void start(SessionFactory sessionFactory) {
        wireMockServer = new WireMockServer(JOPHIEL_PORT);
        wireMockServer.start();
        configureFor(JOPHIEL_PORT);

        // TODO(fushar): this should be in @BeforeAll to avoid duplicate inserts
        AdminRoleDao adminRoleDao = new AdminRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());
        ContestContestantDao contestantDao = new ContestContestantHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        RoleStore roleStore = new RoleStore(adminRoleDao, contestantDao);
        roleStore.addAdmin("adminJid");
    }

    @AfterAll static void shutdown() {
        wireMockServer.shutdown();
    }

    @Test void basic_flow() throws JsonProcessingException {
        // default user
        stubFor(get("/api/v2/users/me")
                .willReturn(okForJson(
                        ImmutableMap.of(
                                "jid", "nonadminJid",
                                "username", "nonadmin",
                                "email", "foo@bar.com")
                )));
        // admin user
        stubFor(get("/api/v2/users/me")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(ADMIN_BEARER_TOKEN))
                .willReturn(okForJson(
                        ImmutableMap.of(
                                "jid", "adminJid",
                                "username", "admin",
                                "email", "foo@bar.com")
                )));

        Contest contestA = contestService.createContest(new ContestData.Builder()
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

        Contest contestB = contestService.createContest(new ContestData.Builder()
                .name("TOKI Open Contest B")
                .description("This is contest B")
                .style(ContestStyle.IOI)
                .build());

        assertThat(contestService.getContest(ADMIN_HEADER, contestB.getJid())).isEqualTo(contestB);

        contestService.createContest(new ContestData.Builder()
                .name("TOKI Open Contest - Testing")
                .description("This is testing contest")
                .style(ContestStyle.IOI)
                .build());
        contestService.createContest(new ContestData.Builder()
                .name("Random Contest")
                .description("This is random contest")
                .style(ContestStyle.IOI)
                .build());

        Page<Contest> contests = contestService.getContests(1, 10);
        assertThat(contests.getData()).containsExactly(contestA, contestB);
    }

}
