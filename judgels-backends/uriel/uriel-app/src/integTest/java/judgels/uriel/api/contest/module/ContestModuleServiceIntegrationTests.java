package judgels.uriel.api.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestModuleServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestModuleService moduleService = createService(ContestModuleService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), CLARIFICATION);

        assertThat(moduleService.getModules(ADMIN_HEADER, contest.getJid())).containsOnly(CLARIFICATION, REGISTRATION);

        moduleService.disableModule(ADMIN_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), VIRTUAL);

        assertThat(moduleService.getModules(ADMIN_HEADER, contest.getJid())).containsOnly(CLARIFICATION, VIRTUAL);
    }
}
