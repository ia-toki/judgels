package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestModuleApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void enable_disable_get_modules() {
        moduleClient.enableModule(managerToken, contest.getJid(), REGISTRATION);
        moduleClient.enableModule(managerToken, contest.getJid(), CLARIFICATION);

        assertThat(moduleClient.getModules(managerToken, contest.getJid()))
                .containsOnly(CLARIFICATION, REGISTRATION);

        moduleClient.disableModule(managerToken, contest.getJid(), REGISTRATION);
        moduleClient.enableModule(managerToken, contest.getJid(), VIRTUAL);

        assertThat(moduleClient.getModules(managerToken, contest.getJid()))
                .containsOnly(CLARIFICATION, VIRTUAL);
    }

    @Test
    void upsert_get_config() {
        moduleClient.enableModule(managerToken, contest.getJid(), VIRTUAL);

        ContestModulesConfig config = moduleClient.getConfig(managerToken, contest.getJid());
        assertThat(config.getVirtual()).isPresent();

        config = new ContestModulesConfig.Builder()
                .from(config)
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(3)).build())
                .build();

        moduleClient.upsertConfig(managerToken, contest.getJid(), config);
        assertThat(moduleClient.getConfig(managerToken, contest.getJid())).isEqualTo(config);
    }
}
