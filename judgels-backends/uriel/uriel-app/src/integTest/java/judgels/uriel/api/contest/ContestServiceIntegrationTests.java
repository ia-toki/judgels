package judgels.uriel.api.contest;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableSet;
import com.palantir.remoting.api.errors.ErrorType;
import java.time.Duration;
import java.time.Instant;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.AbstractServiceIntegrationTests;
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
        Contest contestA = contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug("contest-a").build());
        contestA = contestService.updateContest(ADMIN_HEADER, contestA.getJid(), new ContestUpdateData.Builder()
                .name("TOKI Open Contest A")
                .slug("contest-a")
                .style(ContestStyle.ICPC)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.ofHours(5))
                .build());
        contestService.updateContestDescription(ADMIN_HEADER, contestA.getJid(), new ContestDescription.Builder()
                .description("This is contest A")
                .build());

        assertThat(contestA.getSlug()).isEqualTo("contest-a");
        assertThat(contestA.getName()).isEqualTo("TOKI Open Contest A");
        assertThat(contestA.getStyle()).isEqualTo(ContestStyle.ICPC);
        assertThat(contestA.getBeginTime()).isEqualTo(Instant.ofEpochSecond(42));
        assertThat(contestA.getDuration()).isEqualTo(Duration.ofHours(5));

        ContestDescription contestDescriptionA =
                contestService.getContestDescription(of(ADMIN_HEADER), contestA.getJid());
        assertThat(contestDescriptionA.getDescription()).isEqualTo("This is contest A");

        assertThat(contestService.getContest(of(ADMIN_HEADER), contestA.getJid())).isEqualTo(contestA);
        assertThat(contestService.getContestBySlug(of(ADMIN_HEADER), contestA.getSlug())).isEqualTo(contestA);

        String contestAJid = contestA.getJid();
        assertThatRemoteExceptionThrownBy(
                () -> contestService.getContest(of(AuthHeader.of("randomToken")), contestAJid))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        Contest contestB = contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug("contest-b").build());

        assertThat(contestService.getContest(of(ADMIN_HEADER), contestB.getJid())).isEqualTo(contestB);
        assertThat(contestService.getContestBySlug(of(ADMIN_HEADER), "" + contestB.getId())).isEqualTo(contestB);

        contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug("contest-testing").build());
        contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug("contest-random").build());

        contestantService.addContestants(
                ADMIN_HEADER,
                contestA.getJid(),
                ImmutableSet.of(USER_A_JID));
        contestantService.addContestants(
                ADMIN_HEADER,
                contestB.getJid(),
                ImmutableSet.of(USER_A_JID, USER_B_JID));

        Page<Contest> contests = contestService.getContests(of(USER_A_HEADER), empty());
        assertThat(contests.getData()).containsExactly(contestB, contestA);

        contests = contestService.getContests(of(USER_B_HEADER), empty());
        assertThat(contests.getData()).containsExactly(contestB);
    }
}
