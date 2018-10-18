package judgels.uriel.api.contest.submission;

import static java.util.Optional.empty;
import static judgels.uriel.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.sandalphon.api.submission.Submission;
import judgels.sandalphon.submission.SubmissionData;
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.contest.submission.ContestSubmissionStore;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestSubmissionModel;
import judgels.uriel.role.AdminRoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(
        urlSuffix = URIEL_JDBC_SUFFIX,
        models = {
                AdminRoleModel.class,
                ContestModel.class,
                ContestContestantModel.class,
                ContestSubmissionModel.class})
class ContestSubmissionServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private ContestService contestService = createService(ContestService.class);
    private ContestContestantService contestantService = createService(ContestContestantService.class);
    private ContestSubmissionService submissionService = createService(ContestSubmissionService.class);

    private ContestSubmissionStore submissionStore;

    @BeforeAll
    static void startMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
    }

    @BeforeEach
    void setUpRoles(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        AdminRoleStore adminRoleStore = component.adminRoleStore();
        adminRoleStore.addAdmin(ADMIN_JID);

        submissionStore = component.contestSubmissionStore();
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
        contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now())
                .build());

        contestantService.addContestants(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A_JID, USER_B_JID));

        submissionStore.createSubmission(new SubmissionData.Builder()
//              .userJid(USER_A_JID)
                .problemJid("problemJid1")
                .containerJid(contest.getJid())
                .gradingLanguage("Cpp11")
                .build(), "Batch");

        ContestSubmissionsResponse response =
                submissionService.getSubmissions(ADMIN_HEADER, contest.getJid(), empty(), empty(), empty());

        Submission submission = response.getData().getData().get(0);
        assertThat(submission.getProblemJid()).isEqualTo("problemJid1");
        assertThat(submission.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(submission.getGradingEngine()).isEqualTo("Batch");
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");
    }
}
