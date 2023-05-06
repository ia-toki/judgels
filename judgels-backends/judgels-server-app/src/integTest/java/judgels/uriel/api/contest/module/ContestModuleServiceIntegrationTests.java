package judgels.uriel.api.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestModuleServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void enable_disable_get_modules() {
        moduleService.enableModule(managerHeader, contest.getJid(), REGISTRATION);
        moduleService.enableModule(managerHeader, contest.getJid(), CLARIFICATION);

        assertThat(moduleService.getModules(managerHeader, contest.getJid()))
                .containsOnly(CLARIFICATION, REGISTRATION);

        moduleService.disableModule(managerHeader, contest.getJid(), REGISTRATION);
        moduleService.enableModule(managerHeader, contest.getJid(), VIRTUAL);

        assertThat(moduleService.getModules(managerHeader, contest.getJid()))
                .containsOnly(CLARIFICATION, VIRTUAL);
    }

    @Test
    void upsert_get_config() {
        moduleService.enableModule(managerHeader, contest.getJid(), VIRTUAL);

        ContestModulesConfig config = moduleService.getConfig(managerHeader, contest.getJid());
        assertThat(config.getVirtual()).isPresent();

        config = new ContestModulesConfig.Builder()
                .from(config)
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(3)).build())
                .build();

        moduleService.upsertConfig(managerHeader, contest.getJid(), config);
        assertThat(moduleService.getConfig(managerHeader, contest.getJid())).isEqualTo(config);
    }
}
