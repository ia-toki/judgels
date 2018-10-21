package judgels.uriel.api.contest.module;

import static judgels.uriel.api.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.role.AdminRoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@WithHibernateSession(
        urlSuffix = URIEL_JDBC_SUFFIX,
        models = {AdminRoleModel.class, ContestModel.class, ContestModuleModel.class})
class ContestModuleServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private ContestService contestService = createService(ContestService.class);
    private ContestModuleService moduleService = createService(ContestModuleService.class);

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

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), CLARIFICATION);

        assertThat(moduleService.getModules(ADMIN_HEADER, contest.getJid()))
                .containsExactlyInAnyOrder(CLARIFICATION, REGISTRATION);

        moduleService.disableModule(ADMIN_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), VIRTUAL);

        assertThat(moduleService.getModules(ADMIN_HEADER, contest.getJid()))
                .containsExactlyInAnyOrder(CLARIFICATION, VIRTUAL);
    }
}
