package judgels.uriel.api.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ContestModuleServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private ContestService contestService = createService(ContestService.class);
    private ContestModuleService moduleService = createService(ContestModuleService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
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

        assertThat(moduleService.getModules(ADMIN_HEADER, contest.getJid())).containsOnly(CLARIFICATION, REGISTRATION);

        moduleService.disableModule(ADMIN_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), VIRTUAL);

        assertThat(moduleService.getModules(ADMIN_HEADER, contest.getJid())).containsOnly(CLARIFICATION, VIRTUAL);
    }
}
