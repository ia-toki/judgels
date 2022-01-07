package judgels.uriel.api.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.FILE;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestModuleServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        // as manager

        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), CLARIFICATION);

        assertThat(moduleService.getModules(MANAGER_HEADER, contest.getJid()))
                .containsOnly(CLARIFICATION, REGISTRATION);

        moduleService.disableModule(MANAGER_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), VIRTUAL);

        assertThat(moduleService.getModules(MANAGER_HEADER, contest.getJid())).containsOnly(CLARIFICATION, VIRTUAL);

        ContestModulesConfig config = moduleService.getConfig(MANAGER_HEADER, contest.getJid());
        assertThat(config.getVirtual()).isPresent();

        moduleService.upsertConfig(MANAGER_HEADER, contest.getJid(), config);

        // as supervisor

        assertThat(moduleService.getModules(SUPERVISOR_HEADER, contest.getJid())).containsOnly(CLARIFICATION, VIRTUAL);

        ContestModulesConfig config2 = moduleService.getConfig(SUPERVISOR_HEADER, contest.getJid());
        assertThat(config2.getVirtual()).isPresent();

        assertThatThrownBy(() -> moduleService.enableModule(SUPERVISOR_HEADER, contest.getJid(), FILE))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(() -> moduleService.disableModule(SUPERVISOR_HEADER, contest.getJid(), FILE))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(() -> moduleService.upsertConfig(SUPERVISOR_HEADER, contest.getJid(), config))
                .hasFieldOrPropertyWithValue("code", 403);
    }
}
