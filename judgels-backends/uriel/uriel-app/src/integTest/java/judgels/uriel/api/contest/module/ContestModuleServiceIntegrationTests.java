package judgels.uriel.api.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestModuleServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void enable_disable_get_modules() {
        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), CLARIFICATION);

        assertThat(moduleService.getModules(MANAGER_HEADER, contest.getJid()))
                .containsOnly(CLARIFICATION, REGISTRATION);

        moduleService.disableModule(MANAGER_HEADER, contest.getJid(), REGISTRATION);
        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), VIRTUAL);

        assertThat(moduleService.getModules(MANAGER_HEADER, contest.getJid()))
                .containsOnly(CLARIFICATION, VIRTUAL);
    }

    @Test
    void upsert_get_config() {
        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), VIRTUAL);

        ContestModulesConfig config = moduleService.getConfig(MANAGER_HEADER, contest.getJid());
        assertThat(config.getVirtual()).isPresent();

        config = new ContestModulesConfig.Builder()
                .from(config)
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(3)).build())
                .build();

        moduleService.upsertConfig(MANAGER_HEADER, contest.getJid(), config);
        assertThat(moduleService.getConfig(MANAGER_HEADER, contest.getJid())).isEqualTo(config);
    }
}
